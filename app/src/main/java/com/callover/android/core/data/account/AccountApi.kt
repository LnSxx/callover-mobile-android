package com.callover.android.core.data.account

import com.callover.android.core.data.account.dto.ChangePasswordRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH

interface AccountApi {
    @PATCH("account/password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequestDto,
    )

    @DELETE("account")
    suspend fun deleteAccount()
}