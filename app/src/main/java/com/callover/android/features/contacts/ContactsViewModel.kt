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
    private val screenState = MutableStateFlow(ContactsScreenState())

    val uiState = combine(
        contactsRepository.observeContacts(),
        screenState,
    ) { contacts, screenState ->
        ContactsUiState(
            contacts = contacts,
            isSyncing = screenState.isSyncing,
            errorMessage = screenState.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ContactsUiState(),
    )

    init {
        syncContacts()
    }

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
                    errorMessage = result.errorMessageOrNull(),
                )
            }
        }
    }
}

private data class ContactsScreenState(
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

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