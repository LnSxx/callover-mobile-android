package com.callover.android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackendErrorDto(
    val statusCode: Int,
    val code: String,
    val message: String,
    val path: String? = null,
    val timestamp: String? = null,
    val errors: List<BackendFieldErrorDto> = emptyList(),
)
