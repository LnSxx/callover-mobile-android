package com.callover.android.features.create_contact

import com.callover.android.core.network.ApiError

fun ApiError.toCreateContactUiState(): CreateContactUiState {
    return when (this) {
        is ApiError.Backend -> {
            val contactUserIdFieldError = fieldErrors
                .firstOrNull { it.field == "contactUserId" }
                ?.message

            val aliasFieldError = fieldErrors
                .firstOrNull { it.field == "alias" }
                ?.message

            CreateContactUiState(
                generalError = if (fieldErrors.isEmpty()) {
                    message
                } else {
                    null
                },
                nameError = aliasFieldError,
                userIdError = contactUserIdFieldError,
            )
        }

        ApiError.Network -> {
            CreateContactUiState(
                generalError = "Network error. Check your connection.",
            )
        }

        ApiError.Unknown -> {
            CreateContactUiState(
                generalError = "Something went wrong.",
            )
        }

        ApiError.Unauthorized -> {
            CreateContactUiState(
                generalError = "Session expired. Please log in again.",
            )
        }
    }
}