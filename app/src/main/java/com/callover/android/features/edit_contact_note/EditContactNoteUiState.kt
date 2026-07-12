package com.callover.android.features.edit_contact_note

import com.callover.android.core.domain.models.Contact

data class EditContactNoteUiState(
    val contact: Contact? = null,
    val oldNote: String = "",
    val newNote: String = "",
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val newNoteError: String? = null,
    val canSubmit: Boolean = false,
)