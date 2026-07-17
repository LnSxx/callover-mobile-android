package com.callover.android.core.calls

import com.callover.android.core.domain.models.CallType

sealed interface CallState {
    data object Idle : CallState

    data class Incoming(
        val fromUserId: String,
        val sdp: String,
        val type: CallType,
        val roomId: String?,
    ) : CallState

    data class Outgoing(
        val toUserId: String,
        val sdp: String,
        val type: CallType,
        val roomId: String?,
    ) : CallState

    data class Connecting(
        val peerUserId: String,
        val type: CallType,
        val roomId: String?,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
    ) : CallState

    data class Active(
        val peerUserId: String,
        val type: CallType,
        val roomId: String?,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
    ) : CallState

    data class Ended(
        val reason: CallEndReason,
    ) : CallState
}