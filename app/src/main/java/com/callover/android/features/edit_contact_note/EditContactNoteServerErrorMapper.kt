package com.callover.android.features.edit_contact_note

import com.callover.android.core.network.ApiError

fun ApiError.toEditContactNoteServerState(): EditContactNoteServerState {
    return when (this) {
        is ApiError.Backend -> EditContactNoteServerState(
            isLoading = false,
            generalError = if (fieldErrors.isEmpty()) message else null,
            newNoteError = fieldErrors
                .firstOrNull { it.field == "note" }
                ?.message,
        )

        ApiError.Network -> EditContactNoteServerState(
            generalError = "Network error. Check your connection.",
        )

        ApiError.Unknown -> EditContactNoteServerState(
            generalError = "Something went wrong.",
        )

        ApiError.Unauthorized -> EditContactNoteServerState(
            generalError = "Session expired. Please log in again.",
        )
    }
}