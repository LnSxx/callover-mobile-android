package com.callover.android.features.edit_contact_name

import com.callover.android.core.domain.models.Contact

data class EditContactNameUiState(
    val contact: Contact? = null,
    val oldName: String = "",
    val newName: String = "",
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val newNameError: String? = null,
    val canSubmit: Boolean = false,
)