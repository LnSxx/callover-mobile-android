package com.callover.android.features.contacts

data class ContactsScreenState(
    val isSyncing: Boolean = true,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null,
)