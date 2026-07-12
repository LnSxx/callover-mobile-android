package com.callover.android.features.contact_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.network.ApiResult
import com.callover.android.features.create_contact.CreateContactEvent
import com.callover.android.features.create_contact.CreateContactUiState
import com.callover.android.features.create_contact.toCreateContactUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
) : ViewModel() {
    private val contactId = MutableStateFlow<String?>(null)

    val actionState = MutableStateFlow(ContactDetailsActionState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = contactId
        .flatMapLatest { id ->
            if (id == null) {
                kotlinx.coroutines.flow.flowOf(
                    ContactDetailsUiState(
                        contact = null,
                        isLoading = true,
                    )
                )
            } else {
                contactsRepository.observeContactById(id)
                    .map { contact ->
                        ContactDetailsUiState(
                            contact = contact,
                            isLoading = false,
                        )
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ContactDetailsUiState(),
        )

    fun setContactId(id: String) {
        if (contactId.value == id) {
            return
        }

        contactId.value = id
    }

    fun toggleFavourite() {
        val contact = uiState.value.contact ?: return

        editModificators(
            isFavourite = !contact.isFavourite,
        )
    }

    fun toggleMuted() {
        val contact = uiState.value.contact ?: return

        editModificators(
            isMuted = !contact.isMuted,
        )
    }

    fun toggleBlocked() {
        val contact = uiState.value.contact ?: return

        editModificators(
            isBlocked = !contact.isBlocked,
        )
    }

    private fun editModificators(
        isFavourite: Boolean? = null,
        isMuted: Boolean? = null,
        isBlocked: Boolean? = null,
    ) {
        val id = contactId.value ?: return

        if (
            isFavourite == null &&
            isMuted == null &&
            isBlocked == null
        ) {
            return
        }

        viewModelScope.launch {
            actionState.value = actionState.value.copy(
                isActionLoading = true,
                actionError = null,
            )

            when (
                val result = contactsRepository.editContact(
                    contactId = id,
                    isFavourite = isFavourite,
                    isMuted = isMuted,
                    isBlocked = isBlocked,
                )
            ) {
                is ApiResult.Success -> {
                    actionState.value = ContactDetailsActionState()
                }

                is ApiResult.Error -> {
                    actionState.value = ContactDetailsActionState(
                        isActionLoading = false,
                        actionError = result.error.toContactDetailsActionError(),
                    )
                }
            }
        }
    }

    fun clearActionError() {
        if (actionState.value.actionError == null) {
            return
        }

        actionState.value = actionState.value.copy(
            actionError = null,
        )
    }
}