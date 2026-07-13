package com.callover.android.core.realtime.presence

import android.util.Log
import com.callover.android.core.realtime.RealtimeEventType
import com.callover.android.core.realtime.RealtimeMessage
import com.callover.android.core.realtime.RealtimeMessageRouter
import com.callover.android.core.realtime.SocketManager
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenceRealtimeDataSource @Inject constructor(
    private val messageRouter: RealtimeMessageRouter,
    private val socketManager: SocketManager,
) {
    val events: Flow<PresenceEvent> = messageRouter.messages
        .mapNotNull { message ->
            message.toPresenceEventOrNull()
        }

    private var isListeningConnectionEvents = false

    private var lastRequestedUserIds: List<String> = emptyList()
    private var lastEmittedUserIds: List<String> = emptyList()

    fun startListeningConnectionEvents() {
        if (isListeningConnectionEvents) return
        isListeningConnectionEvents = true

        socketManager.on(Socket.EVENT_CONNECT) {
            emitSubscribeIfNeeded(force = true)
        }
    }

    fun stopListeningConnectionEvents() {
        isListeningConnectionEvents = false

        socketManager.off(Socket.EVENT_CONNECT)

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
            Log.d(TAG, "skip subscribe: empty user ids")
            return
        }

        if (!force && lastRequestedUserIds == lastEmittedUserIds) {
            Log.d(TAG, "skip subscribe: already emitted")
            return
        }

        val payload = JSONObject().apply {
            put("userIds", JSONArray(lastRequestedUserIds))
        }

        Log.d(
            TAG,
            "emit ${RealtimeEventType.PresenceSubscribe.type}, force=$force, payload=$payload",
        )

        val emitted = socketManager.emit(
            event = RealtimeEventType.PresenceSubscribe.type,
            data = payload,
        )

        Log.d(TAG, "subscribe emitted=$emitted")

        if (emitted) {
            lastEmittedUserIds = lastRequestedUserIds
        }
    }

    private fun RealtimeMessage.toPresenceEventOrNull(): PresenceEvent? {
        return when (type) {
            RealtimeEventType.PresenceInitial.type -> {
                payload.toPresenceInitialOrNull()
            }

            RealtimeEventType.PresenceUserOnline.type -> {
                payload.toPresenceUserOnlineOrNull()
            }

            RealtimeEventType.PresenceUserOffline.type -> {
                payload.toPresenceUserOfflineOrNull()
            }

            else -> null
        }
    }

    private fun JSONObject.toPresenceInitialOrNull(): PresenceEvent.Initial {
        val array = optJSONArray("onlineUserIds") ?: JSONArray()

        return PresenceEvent.Initial(
            onlineUserIds = array.toStringSet(),
        )
    }

    private fun JSONObject.toPresenceUserOnlineOrNull(): PresenceEvent.UserOnline? {
        val userId = optString("userId")

        if (userId.isBlank()) {
            return null
        }

        return PresenceEvent.UserOnline(
            userId = userId,
        )
    }

    private fun JSONObject.toPresenceUserOfflineOrNull(): PresenceEvent.UserOffline? {
        val userId = optString("userId")

        if (userId.isBlank()) {
            return null
        }

        return PresenceEvent.UserOffline(
            userId = userId,
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

    companion object {
        private const val TAG = "CalloverPresence"
    }
}