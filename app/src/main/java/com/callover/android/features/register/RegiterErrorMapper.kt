package com.callover.android.features.register

import com.callover.android.core.network.ApiError

fun ApiError.toRegisterUiState(): RegisterUiState {
    return when (this) {
        is ApiError.Backend -> {
            RegisterUiState(
                generalError = if (fieldErrors.isEmpty()) message else null,
                usernameError = fieldErrors
                    .firstOrNull { it.field == "username" }
                    ?.message,
                passwordError = fieldErrors
                    .firstOrNull { it.field == "password" }
                    ?.message,
            )
        }

        ApiError.Network -> {
            RegisterUiState(
                generalError = "Network error. Check your connection.",
            )
        }

        ApiError.Unknown -> {
            RegisterUiState(
                generalError = "Something went wrong.",
            )
        }
    }
}