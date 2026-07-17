package com.callover.android.core.realtime.signaling

import android.util.Log
import com.callover.android.core.data.calls.mappers.toCallTypeOrNull
import com.callover.android.core.realtime.RealtimeEventType
import com.callover.android.core.realtime.RealtimeMessage
import com.callover.android.core.realtime.RealtimeMessageRouter
import com.callover.android.core.realtime.signaling.mappers.toCallTimeoutReasonOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalingRealtimeDataSource @Inject constructor(
    private val messageRouter: RealtimeMessageRouter,
) {
    val events: Flow<SignalingEvent> = messageRouter.messages
        .mapNotNull { message ->
            message.toSignalingEventOrNull()
        }

    private fun RealtimeMessage.toSignalingEventOrNull(): SignalingEvent? {
        return when (type) {
            RealtimeEventType.CallOffer.type -> {
                payload.toCallOfferOrNull()
            }

            RealtimeEventType.CallAnswer.type -> {
                payload.toCallAnswerOrNull()
            }

            RealtimeEventType.CallDecline.type -> {
                payload.toCallDeclineOrNull()
            }

            RealtimeEventType.CallCancel.type -> {
                payload.toCallCancelOrNull()
            }

            RealtimeEventType.CallEnd.type -> {
                payload.toCallEndOrNull()
            }

            RealtimeEventType.CallTimeout.type -> {
                payload.toCallTimeoutOrNull()
            }

            RealtimeEventType.CallIceCandidate.type -> {
                payload.toCallIceCandidateOrNull()
            }

            else -> null
        }
    }

    private fun JSONObject.toCallOfferOrNull(): SignalingEvent.CallOffer? {
        val fromUserId = optString("fromUserId")
        val sdp = optString("sdp")
        val callType = optString("type").toCallTypeOrNull()

        if (
            fromUserId.isBlank() ||
            sdp.isBlank() ||
            callType == null
        ) {
            Log.d(TAG, "Invalid call.offer payload=$this")
            return null
        }

        return SignalingEvent.CallOffer(
            fromUserId = fromUserId,
            sdp = sdp,
            type = callType,
        )
    }

    private fun JSONObject.toCallAnswerOrNull(): SignalingEvent.CallAnswer? {
        val fromUserId = optString("fromUserId")
        val sdp = optString("sdp")

        if (
            fromUserId.isBlank() ||
            sdp.isBlank()
        ) {
            Log.d(TAG, "Invalid call.answer payload=$this")
            return null
        }

        return SignalingEvent.CallAnswer(
            fromUserId = fromUserId,
            sdp = sdp,
        )
    }

    private fun JSONObject.toCallDeclineOrNull(): SignalingEvent.CallDecline? {
        val fromUserId = optString("fromUserId")

        if (fromUserId.isBlank()) {
            Log.d(TAG, "Invalid call.decline payload=$this")
            return null
        }

        return SignalingEvent.CallDecline(
            fromUserId = fromUserId,
        )
    }

    private fun JSONObject.toCallCancelOrNull(): SignalingEvent.CallCancel? {
        val fromUserId = optString("fromUserId")

        if (fromUserId.isBlank()) {
            Log.d(TAG, "Invalid call.cancel payload=$this")
            return null
        }

        return SignalingEvent.CallCancel(
            fromUserId = fromUserId,
        )
    }

    private fun JSONObject.toCallEndOrNull(): SignalingEvent.CallEnd? {
        val fromUserId = optString("fromUserId")

        if (fromUserId.isBlank()) {
            Log.d(TAG, "Invalid call.end payload=$this")
            return null
        }

        return SignalingEvent.CallEnd(
            fromUserId = fromUserId,
        )
    }

    private fun JSONObject.toCallTimeoutOrNull(): SignalingEvent.CallTimeout? {
        val roomId = optString("roomId")
        val reason = optString("reason").toCallTimeoutReasonOrNull()

        if (
            roomId.isBlank() ||
            reason == null
        ) {
            Log.d(TAG, "Invalid call.timeout payload=$this")
            return null
        }

        return SignalingEvent.CallTimeout(
            roomId = roomId,
            reason = reason,
        )
    }

    private fun JSONObject.toCallIceCandidateOrNull(): SignalingEvent.CallIceCandidate? {
        val fromUserId = optString("fromUserId")
        val sdp = optString("sdp")

        if (
            fromUserId.isBlank() ||
            sdp.isBlank() ||
            !has("sdpMLineIndex")
        ) {
            Log.d(TAG, "Invalid call.ice-candidate payload=$this")
            return null
        }

        return SignalingEvent.CallIceCandidate(
            fromUserId = fromUserId,
            sdp = sdp,
            sdpMLineIndex = optInt("sdpMLineIndex"),
            sdpMid = optString("sdpMid").takeIf { it.isNotBlank() },
        )
    }

    companion object {
        private const val TAG = "CalloverSignaling"
    }
}