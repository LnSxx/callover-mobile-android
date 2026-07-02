package com.callover.android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackendFieldErrorDto(
    val field: String,
    val code: String,
    val message: String,
)
