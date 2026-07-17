package com.callover.android.core.data.calls.dto

import kotlinx.serialization.Serializable

@Serializable
data class CallDto(
    val type: String,
    val userId: String,
    val peerUserId: String,
    val roomId: String,
    val status: String,
    val createdAt: String,
    val acceptedAt: String? = null,
    val remoteDescription: RemoteDescriptionDto? = null,
)