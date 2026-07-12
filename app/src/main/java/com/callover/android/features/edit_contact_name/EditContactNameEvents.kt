package com.callover.android.features.edit_contact_name

sealed interface EditContactNameEvent {
    data class ContactNameEdited(
        val contactId: String,
    ) : EditContactNameEvent
}