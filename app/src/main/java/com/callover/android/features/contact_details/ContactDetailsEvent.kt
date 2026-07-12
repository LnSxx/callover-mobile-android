package com.callover.android.features.contact_details

sealed interface ContactDetailsEvent {
    class ContactDeleted() : ContactDetailsEvent
}