package com.callover.android.features.edit_contact_name

data class EditContactNameServerState(
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val newNameError: String? = null,
)