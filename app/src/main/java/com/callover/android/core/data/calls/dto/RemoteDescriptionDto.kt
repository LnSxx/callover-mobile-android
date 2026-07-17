package com.callover.android.core.data.calls.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteDescriptionDto(
    val type: String,
    val sdp: String,
)