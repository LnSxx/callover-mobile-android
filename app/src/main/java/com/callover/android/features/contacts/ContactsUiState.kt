package com.callover.android.features.contacts

import com.callover.android.core.domain.models.Contact

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val onlineUserIds: Set<String> = emptySet(),
    val isSyncing: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null,
)