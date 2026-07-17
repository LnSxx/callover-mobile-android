package com.callover.android.core.data.calls.dto

import kotlinx.serialization.Serializable

@Serializable
data class PendingIceCandidateDto(
    val fromUserId: String,
    val sdp: String,
    val sdpMLineIndex: Int,
    val sdpMid: String? = null,
)