package com.callover.android.features.edit_contact_name

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.network.ApiResult
import com.callover.android.features.create_contact.MAX_CONTACT_NAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditContactNameViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val contactId: String = checkNotNull(savedStateHandle["contactId"])

    private val newName = MutableStateFlow("")
    private val _serverState = MutableStateFlow(EditContactNameServerState())

    private var initialNameWasSet = false

    private val _events = Channel<EditContactNameEvent>()
    val events = _events.receiveAsFlow()

    private val contact = contactsRepository
        .observeContactById(contactId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val uiState: StateFlow<EditContactNameUiState> =
        combine(
            contact,
            newName,
            _serverState,
        ) { contact, newName, serverState ->
            val oldName = contact?.alias.orEmpty()

            EditContactNameUiState(
                contact = contact,
                oldName = oldName,
                newName = newName,
                isLoading = serverState.isLoading,
                generalError = serverState.generalError,
                newNameError = serverState.newNameError,
                canSubmit = contact != null &&
                        newName.trim().isNotBlank() &&
                        newName.trim() != oldName.trim() &&
                        !serverState.isLoading,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = EditContactNameUiState(),
            )

    init {
        viewModelScope.launch {
            contact.collect { contact ->
                if (!initialNameWasSet && contact != null) {
                    initialNameWasSet = true
                    newName.value = contact.alias
                }
            }
        }
    }

    fun onNameChange(value: String) {
        newName.value = value.take(MAX_CONTACT_NAME_LENGTH)
        clearErrors()
    }

    fun editName() {
        val currentState = uiState.value

        if (!currentState.canSubmit) {
            return
        }

        viewModelScope.launch {
            _serverState.value = _serverState.value.copy(
                isLoading = true,
                generalError = null,
                newNameError = null,
            )

            when (
                val result = contactsRepository.editContact(
                    contactId = contactId,
                    alias = currentState.newName.trim(),
                )
            ) {
                is ApiResult.Success -> {
                    _serverState.value = EditContactNameServerState()

                    _events.send(
                        EditContactNameEvent.ContactNameEdited(
                            contactId = result.data.id,
                        ),
                    )
                }

                is ApiResult.Error -> {
                    _serverState.value = result.error.toEditContactNameServerState()
                }
            }
        }
    }

    fun clearErrors() {
        val currentState = _serverState.value

        if (
            currentState.generalError == null &&
            currentState.newNameError == null
        ) {
            return
        }

        _serverState.value = currentState.copy(
            generalError = null,
            newNameError = null,
        )
    }
}