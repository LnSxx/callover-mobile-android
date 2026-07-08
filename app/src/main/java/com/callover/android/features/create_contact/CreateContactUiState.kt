package com.callover.android.features.create_contact

data class CreateContactUiState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val nameError: String? = null,
    val userIdError: String? = null,
)