package com.callover.android.features.change_password

import com.callover.android.core.network.ApiError

fun ApiError.toChangePasswordUiState(): ChangePasswordUiState {
    return when (this) {
        is ApiError.Backend -> {
            val newPasswordFieldError = fieldErrors
                .firstOrNull { it.field == "newPassword" }
                ?.message

            ChangePasswordUiState(
                generalError = if (fieldErrors.isEmpty()) {
                    message
                } else {
                    null
                },
                newPasswordError = newPasswordFieldError
            )
        }

        ApiError.Network -> {
            ChangePasswordUiState(
                generalError = "Network error. Check your connection.",
            )
        }

        ApiError.Unknown -> {
            ChangePasswordUiState(
                generalError = "Something went wrong.",
            )
        }

        ApiError.Unauthorized -> {
            ChangePasswordUiState(
                generalError = "Session expired. Please log in again.",
            )
        }
    }
}