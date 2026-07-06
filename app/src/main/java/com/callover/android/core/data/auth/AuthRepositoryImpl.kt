package com.callover.android.core.data.auth

import com.callover.android.core.data.auth.dto.LoginRequestDto
import com.callover.android.core.data.auth.dto.RegisterRequestDto
import com.callover.android.core.domain.models.User
import com.callover.android.core.data.auth.mappers.toDomain
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val json: Json,
) : AuthRepository {
    override suspend fun login(
        username: String,
        password: String,
    ): ApiResult<User> {
        return safeApiCall(json) {
            authApi.login(
                LoginRequestDto(
                    username = username,
                    password = password,
                )
            ).user.toDomain()
        }
    }

    override suspend fun register(
        username: String,
        password: String,
    ): ApiResult<User> {
        return safeApiCall(json) {
            authApi.register(
                RegisterRequestDto(
                    username = username,
                    password = password,
                )
            ).user.toDomain()
        }
    }

    override suspend fun logout(): ApiResult<Unit> {
        return safeApiCall(json) {
            authApi.logout()
        }
    }
}