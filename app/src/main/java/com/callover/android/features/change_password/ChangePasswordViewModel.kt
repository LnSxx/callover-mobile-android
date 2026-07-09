package com.callover.android.features.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.auth.SessionManager
import com.callover.android.core.data.account.AccountRepository
import com.callover.android.core.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun changePassword(
        currentPassword: String,
        newPassword: String,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                generalError = null,
                newPasswordError = null,
            )

            when (
                val result = accountRepository.changePassword(
                    password = currentPassword,
                    newPassword = newPassword,
                )
            ) {
                is ApiResult.Success -> {
                    _uiState.value = ChangePasswordUiState()

                    sessionManager.forceLogout()
                }

                is ApiResult.Error -> {
                    _uiState.value = result.error
                        .toChangePasswordUiState()
                        .copy(isLoading = false)
                }
            }
        }
    }

    fun clearErrors() {
        val currentState = _uiState.value

        if (
            currentState.generalError == null &&
            currentState.newPasswordError == null
        ) {
            return
        }

        _uiState.value = currentState.copy(
            generalError = null,
            newPasswordError = null,
        )
    }
}