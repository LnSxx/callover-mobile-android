package com.callover.android.features.change_password

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val newPasswordError: String? = null,
)