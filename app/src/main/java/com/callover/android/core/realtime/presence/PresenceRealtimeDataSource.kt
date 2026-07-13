package com.callover.android.core.realtime.presence

import com.callover.android.core.realtime.RealtimeEventType
import com.callover.android.core.realtime.SocketManager
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenceRealtimeDataSource @Inject constructor(
    private val socketManager: SocketManager,
) {
    private val _events = MutableSharedFlow<PresenceEvent>(
        extraBufferCapacity = 64,
    )

    val events: SharedFlow<PresenceEvent> = _events

    private var isListening = false

    private var lastRequestedUserIds: List<String> = emptyList()
    private var lastEmittedUserIds: List<String> = emptyList()

    fun startListening() {
        if (isListening) return
        isListening = true

        socketManager.on(Socket.EVENT_CONNECT) {
            emitSubscribeIfNeeded(force = true)
        }

        socketManager.on(RealtimeEventType.PresenceInitial.type) { args ->
            val payload = args.firstOrNull() as? JSONObject ?: return@on
            val array = payload.optJSONArray("onlineUserIds") ?: return@on

            val onlineUserIds = buildSet {
                for (index in 0 until array.length()) {
                    val userId = array.optString(index)
                    if (userId.isNotBlank()) {
                        add(userId)
                    }
                }
            }

            _events.tryEmit(
                PresenceEvent.Initial(
                    onlineUserIds = onlineUserIds,
                ),
            )
        }

        socketManager.on(RealtimeEventType.PresenceUserOnline.type) { args ->
            val payload = args.firstOrNull() as? JSONObject ?: return@on
            val userId = payload.optString("userId")

            if (userId.isBlank()) {
                return@on
            }

            _events.tryEmit(
                PresenceEvent.UserOnline(
                    userId = userId,
                ),
            )
        }

        socketManager.on(RealtimeEventType.PresenceUserOffline.type) { args ->
            val payload = args.firstOrNull() as? JSONObject ?: return@on
            val userId = payload.optString("userId")

            if (userId.isBlank()) {
                return@on
            }

            _events.tryEmit(
                PresenceEvent.UserOffline(
                    userId = userId,
                ),
            )
        }
    }

    fun stopListening() {
        isListening = false

        socketManager.off(Socket.EVENT_CONNECT)
        socketManager.off(RealtimeEventType.PresenceInitial.type)
        socketManager.off(RealtimeEventType.PresenceUserOnline.type)
        socketManager.off(RealtimeEventType.PresenceUserOffline.type)

        lastRequestedUserIds = emptyList()
        lastEmittedUserIds = emptyList()
    }

    fun subscribeToUsers(
        userIds: List<String>,
    ) {
        val normalizedUserIds = userIds
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        lastRequestedUserIds = normalizedUserIds

        emitSubscribeIfNeeded()
    }

    private fun emitSubscribeIfNeeded(
        force: Boolean = false,
    ) {
        if (lastRequestedUserIds.isEmpty()) {
            return
        }

        if (!force && lastRequestedUserIds == lastEmittedUserIds) {
            return
        }

        val payload = JSONObject().apply {
            put("userIds", JSONArray(lastRequestedUserIds))
        }

        socketManager.emit(
            event = RealtimeEventType.PresenceSubscribe.type,
            data = payload,
        )

        lastEmittedUserIds = lastRequestedUserIds
    }
}