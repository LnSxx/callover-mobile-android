package com.callover.android.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.auth.SessionManager
import com.callover.android.core.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun register(
        username: String,
        password: String,
    ) {
        if (_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)

            when (
                val result = sessionManager.register(
                    username = username,
                    password = password,
                )
            ) {
                is ApiResult.Success -> {
                    _uiState.value = RegisterUiState()
                }

                is ApiResult.Error -> {
                    _uiState.value = result.error.toRegisterUiState()
                }
            }
        }
    }

    fun clearErrors() {
        val currentState = _uiState.value

        if (
            currentState.generalError == null &&
            currentState.usernameError == null &&
            currentState.passwordError == null
        ) {
            return
        }

        _uiState.value = currentState.copy(
            generalError = null,
            usernameError = null,
            passwordError = null,
        )
    }
}