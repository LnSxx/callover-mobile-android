package com.callover.android.core.data.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestDto(
    val password: String,
    val newPassword: String,
)