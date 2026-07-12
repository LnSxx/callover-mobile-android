package com.callover.android.features.edit_contact_note

data class EditContactNoteServerState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val newNoteError: String? = null,
)