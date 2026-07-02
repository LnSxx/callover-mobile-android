package com.callover.android.core.data.auth

import com.callover.android.core.domain.models.User
import com.callover.android.core.network.ApiResult

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String,
    ): ApiResult<User>

    suspend fun register(
        username: String,
        password: String,
    ): ApiResult<User>

    suspend fun logout(): ApiResult<Unit>
}