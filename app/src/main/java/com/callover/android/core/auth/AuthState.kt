package com.callover.android.core.auth

import com.callover.android.core.domain.models.User


sealed interface AuthState {
    data object Loading : AuthState

    data object Unauthenticated : AuthState

    data class Authenticated(
        val user: User,
        val isOffline: Boolean = false,
    ) : AuthState
}