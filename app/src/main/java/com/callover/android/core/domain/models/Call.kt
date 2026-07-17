package com.callover.android.core.domain.models

data class Call(
    val type: CallType,
    val userId: String,
    val peerUserId: String,
    val roomId: String,
    val status: CallStatus,
    val createdAt: String,
    val acceptedAt: String?,
    val remoteDescription: RemoteDescription?,
)