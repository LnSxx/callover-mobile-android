package com.callover.android.features.delete_account

import com.callover.android.core.network.ApiError
import com.callover.android.features.change_password.ChangePasswordUiState

fun ApiError.toDeleteAccountUiState(): DeleteAccountUiState {
    return when (this) {
        is ApiError.Backend -> {
            DeleteAccountUiState(
                generalError = message
            )
        }

        ApiError.Network -> {
            DeleteAccountUiState(
                generalError = "Network error. Check your connection.",
            )
        }

        ApiError.Unknown -> {
            DeleteAccountUiState(
                generalError = "Something went wrong.",
            )
        }

        ApiError.Unauthorized -> {
            DeleteAccountUiState(
                generalError = "Session expired. Please log in again.",
            )
        }
    }
}