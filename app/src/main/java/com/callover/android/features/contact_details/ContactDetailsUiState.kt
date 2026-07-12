package com.callover.android.features.contact_details

import com.callover.android.core.domain.models.Contact

data class ContactDetailsUiState(
    val contact: Contact? = null,
    val isLoading: Boolean = true,
)