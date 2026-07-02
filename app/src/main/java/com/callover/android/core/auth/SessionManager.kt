package com.callover.android.core.auth

import com.callover.android.core.data.auth.AuthRepository
import com.callover.android.core.data.profile.ProfileRepository
import com.callover.android.core.domain.models.User
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.PersistentCalloverCookieJar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val cookieJar: PersistentCalloverCookieJar,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun restoreSession() {
        _authState.value = AuthState.Loading

        when (val result = profileRepository.getMe()) {
            is ApiResult.Success -> {
                _authState.value = AuthState.Authenticated(result.data)
            }

            is ApiResult.Error -> {
                cookieJar.clear()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    suspend fun login(
        username: String,
        password: String,
    ): ApiResult<User> {
        val result = authRepository.login(
            username = username,
            password = password,
        )

        if (result is ApiResult.Success) {
            _authState.value = AuthState.Authenticated(result.data)
        }

        return result
    }

    suspend fun register(
        username: String,
        password: String,
    ): ApiResult<User> {
        val result = authRepository.register(
            username = username,
            password = password,
        )

        if (result is ApiResult.Success) {
            _authState.value = AuthState.Authenticated(result.data)
        }

        return result
    }

    suspend fun logout() {
        authRepository.logout()
        cookieJar.clear()
        _authState.value = AuthState.Unauthenticated
    }

    fun forceLogout() {
        cookieJar.clear()
        _authState.value = AuthState.Unauthenticated
    }
}