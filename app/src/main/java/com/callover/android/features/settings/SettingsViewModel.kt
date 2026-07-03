package com.callover.android.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.auth.AuthState
import com.callover.android.core.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    sessionManager: SessionManager,
) : ViewModel() {
    val uiState = sessionManager.authState
        .map { authState ->
            when (authState) {
                AuthState.Loading -> {
                    SettingsUiState(
                        isLoading = true,
                    )
                }

                AuthState.Unauthenticated -> {
                    SettingsUiState(
                        isLoading = false,
                    )
                }

                is AuthState.Authenticated -> {
                    SettingsUiState(
                        username = authState.user.username,
                        userId = authState.user.id,
                        isLoading = false,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )
}