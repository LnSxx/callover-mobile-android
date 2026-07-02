package com.callover.android.core.auth

import com.callover.android.core.data.auth.AuthRepository
import com.callover.android.core.data.profile.ProfileRepository
import com.callover.android.core.domain.models.User
import com.callover.android.core.network.ApiError
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.PersistentCalloverCookieJar
import com.callover.android.core.storage.user.UserStorage
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
    private val userStorage: UserStorage,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun restoreSession() {
        _authState.value = AuthState.Loading

        when (val result = profileRepository.getMe()) {
            is ApiResult.Success -> {
                saveAuthenticatedUser(result.data)
            }

            is ApiResult.Error -> {
                when (result.error) {
                    ApiError.Unauthorized -> clearLocalSession()
                    else -> restoreCachedUserOrUnauthenticated()
                }
            }
        }
    }

    suspend fun login(
        username: String,
        password: String,
    ): ApiResult<User> {
        val result = authRepository.login(username, password)

        if (result is ApiResult.Success) {
            saveAuthenticatedUser(result.data)
        }

        return result
    }

    suspend fun register(
        username: String,
        password: String,
    ): ApiResult<User> {
        val result = authRepository.register(username, password)

        if (result is ApiResult.Success) {
            saveAuthenticatedUser(result.data)
        }

        return result
    }

    suspend fun logout() {
        authRepository.logout()
        clearLocalSession()
    }

    fun forceLogout() {
        clearLocalSession()
    }

    private fun saveAuthenticatedUser(user: User) {
        userStorage.saveUser(user)
        _authState.value = AuthState.Authenticated(
            user = user,
            isOffline = false,
        )
    }

    private fun restoreCachedUserOrUnauthenticated() {
        val cachedUser = userStorage.loadUser()

        _authState.value = if (cachedUser != null) {
            AuthState.Authenticated(
                user = cachedUser,
                isOffline = true,
            )
        } else {
            AuthState.Unauthenticated
        }
    }

    private fun clearLocalSession() {
        cookieJar.clear()
        userStorage.clearUser()
        _authState.value = AuthState.Unauthenticated
    }
}