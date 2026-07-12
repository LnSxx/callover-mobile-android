package com.callover.android.features.edit_contact_note

sealed interface EditContactNoteEvent {
    data class ContactNoteEdited(
        val contactId: String,
    ) : EditContactNoteEvent
}