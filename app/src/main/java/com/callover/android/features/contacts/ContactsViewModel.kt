package com.callover.android.features.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.network.ApiError
import com.callover.android.core.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
) : ViewModel() {
    private val screenState = MutableStateFlow(
        ContactsScreenState(
            isSyncing = true,
            hasLoaded = false,
        )
    )

    val uiState = combine(
        contactsRepository.observeContacts(),
        contactsRepository.observeIsSyncing(),
        screenState,
    ) { contacts, isSyncing, screenState ->
        ContactsUiState(
            contacts = contacts,
            isSyncing = isSyncing,
            hasLoaded = true,
            errorMessage = screenState.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ContactsUiState(
            contacts = emptyList(),
            isSyncing = true,
            hasLoaded = false,
            errorMessage = null,
        ),
    )

    fun syncContacts() {
        viewModelScope.launch {
            screenState.update {
                it.copy(
                    isSyncing = true,
                    errorMessage = null,
                )
            }

            val result = contactsRepository.syncContacts()

            screenState.update {
                it.copy(
                    isSyncing = false,
                    hasLoaded = true,
                    errorMessage = result.errorMessageOrNull(),
                )
            }
        }
    }
}

private fun ApiResult<Unit>.errorMessageOrNull(): String? {
    return when (this) {
        is ApiResult.Success -> null
        is ApiResult.Error -> {
            when (error) {
                ApiError.Network -> "Network error. Showing saved contacts."
                ApiError.Unknown -> "Something went wrong."
                ApiError.Unauthorized -> null
                is ApiError.Backend -> error.message
            }
        }
    }
}