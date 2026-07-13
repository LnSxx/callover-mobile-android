package com.callover.android.core.signaling

import com.callover.android.core.domain.models.CallType
import com.callover.android.core.domain.models.toPayloadValue
import com.callover.android.core.network.ApiError
import com.callover.android.core.network.ApiResult
import com.callover.android.core.realtime.RealtimeEventType
import com.callover.android.core.realtime.SocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalingServiceImpl @Inject constructor(
    private val socketManager: SocketManager,
) : SignalingService {

    override suspend fun sendOffer(
        toUserId: String,
        sdp: String,
        type: CallType,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)
            put("sdp", sdp)
            put("type", type.toPayloadValue())
        }

        return sendEvent(
            event = RealtimeEventType.CallOffer.type,
            payload = payload,
        )
    }

    override suspend fun sendAnswer(
        toUserId: String,
        sdp: String,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)
            put("sdp", sdp)
        }

        return sendEvent(
            event = RealtimeEventType.CallAnswer.type,
            payload = payload,
        )
    }

    override suspend fun sendDecline(
        toUserId: String,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)
        }

        return sendEvent(
            event = RealtimeEventType.CallDecline.type,
            payload = payload,
        )
    }

    override suspend fun sendCancel(
        toUserId: String,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)
        }

        return sendEvent(
            event = RealtimeEventType.CallCancel.type,
            payload = payload,
        )
    }

    override suspend fun sendEnd(
        toUserId: String,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)
        }

        return sendEvent(
            event = RealtimeEventType.CallEnd.type,
            payload = payload,
        )
    }

    override suspend fun sendIceCandidate(
        toUserId: String,
        sdp: String,
        sdpMLineIndex: Int,
        sdpMid: String?,
    ): ApiResult<Unit> {
        val payload = JSONObject().apply {
            put("toUserId", toUserId)

            put("sdp", sdp)

            put("sdpMLineIndex", sdpMLineIndex)

            if (sdpMid != null) {
                put("sdpMid", sdpMid)
            }
        }

        return sendEvent(
            event = RealtimeEventType.CallIceCandidate.type,
            payload = payload,
        )
    }

    private suspend fun sendEvent(
        event: String,
        payload: JSONObject,
    ): ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val emitted = socketManager.emit(
                    event = event,
                    data = payload,
                )

                if (emitted) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(ApiError.Network)
                }
            } catch (error: Throwable) {
                ApiResult.Error(ApiError.Unknown)
            }
        }
    }
}