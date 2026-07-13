package com.callover.android.core.realtime

import android.util.Log
import com.callover.android.core.network.ApiConfig
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    private var socket: Socket? = null

    fun connect() {
        if (socket?.connected() == true) {
            return
        }

        val options = IO.Options.builder()
            .setPath("/socket.io")
            .setTransports(arrayOf("websocket"))
            .setForceNew(false)
            .setReconnection(true)
            .setReconnectionAttempts(Int.MAX_VALUE)
            .setReconnectionDelay(1_000)
            .setReconnectionDelayMax(10_000)
            .build()

        options.callFactory = okHttpClient
        options.webSocketFactory = okHttpClient

        val baseUrl = ApiConfig.BASE_URL.trimEnd('/')
        val socketUrl = "$baseUrl/events"

        socket = IO.socket(socketUrl, options).apply {
            on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket connected: ${id()}")
            }

            on(Socket.EVENT_DISCONNECT) { args ->
                Log.d(TAG, "Socket disconnected: ${args.joinToString()}")
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Socket connect error: ${args.joinToString()}")
            }

            connect()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
    }

    fun emit(
        event: String,
        data: Any,
    ) {
        val currentSocket = socket ?: return

        if (!currentSocket.connected()) {
            return
        }

        currentSocket.emit(event, data)
    }

    fun on(
        event: String,
        listener: (Array<Any>) -> Unit,
    ) {
        socket?.on(event) { args ->
            listener(args)
        }
    }

    fun off(event: String) {
        socket?.off(event)
    }

    companion object {
        private const val TAG = "CalloverSocket"
    }
}