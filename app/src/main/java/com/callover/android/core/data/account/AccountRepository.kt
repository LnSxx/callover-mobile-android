package com.callover.android.core.data.account

import com.callover.android.core.network.ApiResult

interface AccountRepository {
    suspend fun changePassword(
        password: String,
        newPassword: String,
    ): ApiResult<Unit>

    suspend fun deleteAccount(): ApiResult<Unit>
}