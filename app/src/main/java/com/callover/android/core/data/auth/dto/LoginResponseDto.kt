package com.callover.android.core.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val user: UserDto
)
