package com.callover.android.core.realtime

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeMessageRouter @Inject constructor(
    private val socketManager: SocketManager,
) {
    private val _messages = MutableSharedFlow<RealtimeMessage>(
        extraBufferCapacity = 128,
    )

    val messages: SharedFlow<RealtimeMessage> = _messages

    private var isListening = false

    fun startListening() {
        if (isListening) return
        isListening = true

        socketManager.on("message") { args ->
            Log.d(TAG, "raw message args=${args.joinToString()}")

            val message = args.firstOrNull() as? JSONObject
            if (message == null) {
                Log.d(TAG, "message is not JSONObject")
                return@on
            }

            val type = message.optString("type")
            val payload = message.optJSONObject("payload") ?: JSONObject()

            Log.d(TAG, "message type=$type payload=$payload")

            if (type.isBlank()) {
                Log.d(TAG, "skip message: blank type")
                return@on
            }

            val emitted = _messages.tryEmit(
                RealtimeMessage(
                    type = type,
                    payload = payload,
                )
            )

            Log.d(TAG, "router emitted=$emitted type=$type")
        }
    }

    fun stopListening() {
        isListening = false
        socketManager.off("message")
    }

    companion object {
        private const val TAG = "RealtimeRouter"
    }
}