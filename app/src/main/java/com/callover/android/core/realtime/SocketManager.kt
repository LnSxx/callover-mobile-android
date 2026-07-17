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

    private fun getOrCreateSocket(): Socket {
        val existingSocket = socket

        if (existingSocket != null) {
            return existingSocket
        }

        val options = IO.Options().apply {
            path = "/socket.io"
            transports = arrayOf("websocket")
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 1_000
            reconnectionDelayMax = 10_000

            callFactory = okHttpClient
            webSocketFactory = okHttpClient
        }

        val socketUrl = "${ApiConfig.BASE_URL.trimEnd('/')}/events"

        val newSocket = IO.socket(socketUrl, options).apply {
            on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket connected: ${id()}")
            }

            on(Socket.EVENT_DISCONNECT) { args ->
                Log.d(TAG, "Socket disconnected: ${args.joinToString()}")
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Socket connect error: ${args.joinToString()}")
            }
        }

        socket = newSocket

        return newSocket
    }

    fun connect() {
        val currentSocket = getOrCreateSocket()

        if (currentSocket.connected()) {
            return
        }

        currentSocket.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun on(
        event: String,
        listener: (Array<Any>) -> Unit,
    ) {
        val currentSocket = getOrCreateSocket()

        Log.d(TAG, "register listener event=$event")

        currentSocket.on(event) { args ->
            listener(args)
        }
    }

    fun off(event: String) {
        socket?.off(event)
    }

    fun emit(
        event: String,
        data: Any,
    ): Boolean {
        val currentSocket = socket

        if (currentSocket == null) {
            Log.d(TAG, "emit skipped, socket is null, event=$event")
            return false
        }

        if (!currentSocket.connected()) {
            Log.d(TAG, "emit skipped, socket not connected, event=$event")
            return false
        }

        Log.d(TAG, "emit event=$event data=$data")
        currentSocket.emit(event, data)

        return true
    }

    companion object {
        private const val TAG = "CalloverSocket"
    }
}