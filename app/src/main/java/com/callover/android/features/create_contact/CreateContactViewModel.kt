package com.callover.android.features.create_contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.network.ApiResult
import com.callover.android.features.login.LoginUiState
import com.callover.android.features.login.toLoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateContactViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateContactUiState())
    val uiState = _uiState.asStateFlow()

    fun createContact(
        name: String,
        userId: String,
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateContactUiState(isLoading = true)

            when (
                val result = contactsRepository.createContact(
                    alias = name,
                    contactUserId = userId,
                )
            ) {
                is ApiResult.Success -> {
                    _uiState.value = CreateContactUiState()
                }

                is ApiResult.Error -> {
                    _uiState.value = result.error.toCreateContactUiState()
                }
            }
        }
    }

    fun clearErrors() {
        val currentState = _uiState.value

        if (
            currentState.generalError == null &&
            currentState.nameError == null &&
            currentState.userIdError == null
        ) {
            return
        }

        _uiState.value = currentState.copy(
            generalError = null,
            nameError = null,
            userIdError = null,
        )
    }
}