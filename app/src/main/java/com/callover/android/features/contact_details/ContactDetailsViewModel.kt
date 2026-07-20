package com.callover.android.features.contact_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.call_coordinator.CallCoordinator
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.domain.models.CallType
import com.callover.android.core.network.ApiResult
import com.callover.android.core.realtime.presence.PresenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val presenceRepository: PresenceRepository,
    private val callCoordinator: CallCoordinator,
) : ViewModel() {
    private val contactId = MutableStateFlow<String?>(null)

    val actionState = MutableStateFlow(ContactDetailsActionState())

    private val _events = Channel<ContactDetailsEvent>()
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contactFlow = contactId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                contactsRepository.observeContactById(id)
            }
        }

    val uiState = combine(
        contactFlow,
        presenceRepository.onlineUserIds,
        actionState,
    ) { contact, onlineUserIds, actionState ->
        ContactDetailsUiState(
            contact = contact,
            isLoading = contactId.value == null,
            isOnline = contact?.contactUserId in onlineUserIds
        )
    }.stateIn(
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

    fun deleteContact() {
        val id = contactId.value ?: return

        viewModelScope.launch {
            actionState.value = actionState.value.copy(
                isActionLoading = true,
                actionError = null,
            )

            when (
                val result = contactsRepository.deleteContact(
                    id = id,
                )
            ) {
                is ApiResult.Success -> {
                    actionState.value = ContactDetailsActionState()

                    _events.send(
                        ContactDetailsEvent.ContactDeleted(),
                    )
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

    fun startAudioCall() {
        startCall(CallType.Audio)
    }

    fun startVideoCall() {
        startCall(CallType.Video)
    }

    fun clearActionError() {
        if (actionState.value.actionError == null) {
            return
        }

        actionState.value = actionState.value.copy(
            actionError = null,
        )
    }

    private fun startCall(type: CallType) {
        val peerUserId = uiState.value.contact?.contactUserId ?: return

        viewModelScope.launch {
            callCoordinator.startOutgoingCall(
                targetUserId = peerUserId,
                type = type,
            )
        }
    }
}