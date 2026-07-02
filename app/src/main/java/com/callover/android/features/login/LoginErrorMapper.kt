package com.callover.android.features.login

import com.callover.android.core.network.ApiError

fun ApiError.toLoginUiState(): LoginUiState {
    return when (this) {
        is ApiError.Backend -> {
            LoginUiState(
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
            LoginUiState(
                generalError = "Network error. Check your connection.",
            )
        }

        ApiError.Unknown -> {
            LoginUiState(
                generalError = "Something went wrong.",
            )
        }
    }
}