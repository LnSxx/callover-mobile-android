package com.callover.android.features.register


data class RegisterUiState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
)