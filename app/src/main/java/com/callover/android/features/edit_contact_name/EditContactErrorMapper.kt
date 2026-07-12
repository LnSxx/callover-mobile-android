package com.callover.android.features.edit_contact_name

import com.callover.android.core.network.ApiError

fun ApiError.toEditContactNameServerState(): EditContactNameServerState {
    return when (this) {
        is ApiError.Backend -> EditContactNameServerState(
            isLoading = false,
            generalError = if (fieldErrors.isEmpty()) message else null,
            newNameError = fieldErrors
                .firstOrNull { it.field == "alias" }
                ?.message,
        )

        ApiError.Network -> EditContactNameServerState(
            generalError = "Network error. Check your connection.",
        )

        ApiError.Unknown -> EditContactNameServerState(
            generalError = "Something went wrong.",
        )

        ApiError.Unauthorized -> EditContactNameServerState(
            generalError = "Session expired. Please log in again.",
        )
    }
}