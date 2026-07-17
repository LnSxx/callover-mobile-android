package com.callover.android.features.call_overlay_host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.call_coordinator.CallCoordinator
import com.callover.android.core.calls.CallState
import com.callover.android.core.data.contacts.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callCoordinator: CallCoordinator,
    private val contactsRepository: ContactsRepository,
) : ViewModel() {
    val uiState: StateFlow<CallUiState> =
        combine(
            callCoordinator.callState,
            contactsRepository.observeContacts(),
        ) { callState, contacts ->
            val peerUserId = callState.peerUserIdOrNull()

            val contact = contacts.firstOrNull { contact ->
                contact.contactUserId == peerUserId
            }

            CallUiState(
                callState = callState,
                peerDisplayName = contact?.alias
                    ?: peerUserId
                    ?: "",
                peerUserId = peerUserId,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CallUiState(),
            )

    fun acceptCall() {
        viewModelScope.launch {
            callCoordinator.acceptCall()
        }
    }

    fun declineCall() {
        viewModelScope.launch {
            callCoordinator.declineCall()
        }
    }

    fun cancelOutgoingCall() {
        viewModelScope.launch {
            callCoordinator.cancelOutgoingCall()
        }
    }

    fun endCall() {
        viewModelScope.launch {
            callCoordinator.endCurrentCall()
        }
    }

    fun toggleMic() {
        callCoordinator.toggleMic()
    }

    fun toggleCamera() {
        callCoordinator.toggleCamera()
    }

    private fun CallState.peerUserIdOrNull(): String? {
        return when (this) {
            CallState.Idle -> null
            is CallState.Incoming -> fromUserId
            is CallState.Outgoing -> toUserId
            is CallState.Connecting -> peerUserId
            is CallState.Active -> peerUserId
            is CallState.Ended -> null
        }
    }
}