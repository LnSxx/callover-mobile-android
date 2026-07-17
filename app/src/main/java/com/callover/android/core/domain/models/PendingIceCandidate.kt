package com.callover.android.core.domain.models

data class PendingIceCandidate(
    val fromUserId: String,
    val sdp: String,
    val sdpMLineIndex: Int,
    val sdpMid: String?,
)