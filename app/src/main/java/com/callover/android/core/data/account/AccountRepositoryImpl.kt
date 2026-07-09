package com.callover.android.core.data.account

import com.callover.android.core.data.account.dto.ChangePasswordRequestDto
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountApi: AccountApi,
    private val json: Json,
) : AccountRepository {
    override suspend fun changePassword(
        password: String,
        newPassword: String,
    ): ApiResult<Unit> {
        return safeApiCall(json) {
            accountApi.changePassword(
                ChangePasswordRequestDto(
                    password = password,
                    newPassword = newPassword,
                )
            )
        }
    }

    override suspend fun deleteAccount(): ApiResult<Unit> {
        return safeApiCall(json) {
            accountApi.deleteAccount()
        }
    }
}