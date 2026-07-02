package com.callover.android.features.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
)