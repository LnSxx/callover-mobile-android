package com.callover.android.features.create_contact

sealed interface CreateContactEvent {
    data class ContactCreated(
        val contactId: String,
    ) : CreateContactEvent
}