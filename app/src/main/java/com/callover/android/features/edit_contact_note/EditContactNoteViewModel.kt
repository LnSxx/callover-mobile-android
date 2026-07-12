package com.callover.android.features.edit_contact_note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.network.ApiResult
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

private const val MAX_CONTACT_NOTE_LENGTH = 500

@HiltViewModel
class EditContactNoteViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val contactId: String = checkNotNull(savedStateHandle["contactId"])

    private val newNote = MutableStateFlow("")
    private val _serverState = MutableStateFlow(EditContactNoteServerState())

    private var initialNoteWasSet = false

    private val _events = Channel<EditContactNoteEvent>()
    val events = _events.receiveAsFlow()

    private val contact = contactsRepository
        .observeContactById(contactId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val uiState: StateFlow<EditContactNoteUiState> =
        combine(
            contact,
            newNote,
            _serverState,
        ) { contact, newNote, serverState ->
            val oldNote = contact?.note.orEmpty()

            EditContactNoteUiState(
                contact = contact,
                oldNote = oldNote,
                newNote = newNote,
                isLoading = serverState.isLoading,
                generalError = serverState.generalError,
                newNoteError = serverState.newNoteError,
                canSubmit = contact != null &&
                        newNote.trim() != oldNote.trim() &&
                        !serverState.isLoading,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = EditContactNoteUiState(),
            )

    init {
        viewModelScope.launch {
            contact.collect { contact ->
                if (!initialNoteWasSet && contact != null) {
                    initialNoteWasSet = true
                    newNote.value = contact.note.orEmpty()
                }
            }
        }
    }

    fun onNoteChange(value: String) {
        newNote.value = value.take(MAX_CONTACT_NOTE_LENGTH)
        clearErrors()
    }

    fun editNote() {
        val currentState = uiState.value

        if (!currentState.canSubmit) {
            return
        }

        viewModelScope.launch {
            _serverState.value = _serverState.value.copy(
                isLoading = true,
                generalError = null,
                newNoteError = null,
            )

            when (
                val result = contactsRepository.editContact(
                    contactId = contactId,
                    note = currentState.newNote.trim(),
                )
            ) {
                is ApiResult.Success -> {
                    _serverState.value = EditContactNoteServerState()

                    _events.send(
                        EditContactNoteEvent.ContactNoteEdited(
                            contactId = result.data.id,
                        ),
                    )
                }

                is ApiResult.Error -> {
                    _serverState.value = result.error.toEditContactNoteServerState()
                }
            }
        }
    }

    fun clearErrors() {
        val currentState = _serverState.value

        if (
            currentState.generalError == null &&
            currentState.newNoteError == null
        ) {
            return
        }

        _serverState.value = currentState.copy(
            generalError = null,
            newNoteError = null,
        )
    }
}