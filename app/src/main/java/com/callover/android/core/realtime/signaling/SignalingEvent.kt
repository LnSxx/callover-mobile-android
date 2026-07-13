package com.callover.android.core.realtime.signaling

import com.callover.android.core.domain.models.CallTimeoutReason
import com.callover.android.core.domain.models.CallType

sealed interface SignalingEvent {
    data class CallOffer(
        val fromUserId: String,
        val sdp: String,
        val type: CallType,
    ) : SignalingEvent

    data class CallAnswer(
        val fromUserId: String,
        val sdp: String,
    ) : SignalingEvent

    data class CallDecline(
        val fromUserId: String,
    ) : SignalingEvent

    data class CallCancel(
        val fromUserId: String,
    ) : SignalingEvent

    data class CallTimeout(
        val roomId: String,
        val reason: CallTimeoutReason,
    ) : SignalingEvent

    data class CallIceCandidate(
        val fromUserId: String,
        val sdp: String,
        val sdpMLineIndex: Int,
        val sdpMid: String?,
    ) : SignalingEvent
}