package com.callover.android.core.realtime

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
            val message = args.firstOrNull() as? JSONObject ?: return@on
            val type = message.optString("type")
            val payload = message.optJSONObject("payload") ?: JSONObject()

            if (type.isBlank()) {
                return@on
            }

            _messages.tryEmit(
                RealtimeMessage(
                    type = type,
                    payload = payload,
                ),
            )
        }
    }

    fun stopListening() {
        isListening = false
        socketManager.off("message")
    }
}