package com.callover.android.core.domain.models

data class RemoteDescription(
    val type: RemoteDescriptionType,
    val sdp: String,
)