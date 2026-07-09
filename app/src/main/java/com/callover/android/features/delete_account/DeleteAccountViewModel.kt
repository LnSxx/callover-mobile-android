package com.callover.android.features.delete_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.auth.SessionManager
import com.callover.android.core.data.account.AccountRepository
import com.callover.android.core.network.ApiResult
import com.callover.android.features.change_password.toChangePasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState = _uiState.asStateFlow()

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                generalError = null,
            )

            when (
                val result = accountRepository.deleteAccount()
            ) {
                is ApiResult.Success -> {
                    _uiState.value = DeleteAccountUiState()

                    sessionManager.forceLogout()
                }

                is ApiResult.Error -> {
                    _uiState.value = result.error
                        .toDeleteAccountUiState()
                        .copy(isLoading = false)
                }
            }
        }
    }
}