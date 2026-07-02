package com.callover.android.core.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String,
    val password: String,
)