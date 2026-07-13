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

        socketManager.on("message") { args ->
            val message = args.firstOrNull() as? JSONObject ?: return@on

            val type = message.optString("type")
            val payload = message.optJSONObject("payload") ?: JSONObject()

            when (type) {
                RealtimeEventType.PresenceInitial.type -> {
                    handlePresenceInitial(payload)
                }

                RealtimeEventType.PresenceUserOnline.type -> {
                    handlePresenceUserOnline(payload)
                }

                RealtimeEventType.PresenceUserOffline.type -> {
                    handlePresenceUserOffline(payload)
                }
            }
        }
    }

    fun stopListening() {
        isListening = false

        socketManager.off(Socket.EVENT_CONNECT)
        socketManager.off("message")

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

        val emitted = socketManager.emit(
            event = RealtimeEventType.PresenceSubscribe.type,
            data = payload,
        )

        if (emitted) {
            lastEmittedUserIds = lastRequestedUserIds
        }
    }

    private fun handlePresenceInitial(
        payload: JSONObject,
    ) {
        val array = payload.optJSONArray("onlineUserIds") ?: JSONArray()

        val onlineUserIds = array.toStringSet()


        _events.tryEmit(
            PresenceEvent.Initial(
                onlineUserIds = onlineUserIds,
            ),
        )
    }

    private fun handlePresenceUserOnline(
        payload: JSONObject,
    ) {
        val userId = payload.optString("userId")

        if (userId.isBlank()) {
            return
        }

        _events.tryEmit(
            PresenceEvent.UserOnline(
                userId = userId,
            ),
        )
    }

    private fun handlePresenceUserOffline(
        payload: JSONObject,
    ) {
        val userId = payload.optString("userId")

        if (userId.isBlank()) {
            return
        }

        _events.tryEmit(
            PresenceEvent.UserOffline(
                userId = userId,
            ),
        )
    }

    private fun JSONArray.toStringSet(): Set<String> {
        return buildSet {
            for (index in 0 until length()) {
                val value = optString(index)

                if (value.isNotBlank()) {
                    add(value)
                }
            }
        }
    }
}