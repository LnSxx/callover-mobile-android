package com.callover.android.core.data.auth

import com.callover.android.core.data.auth.dto.LoginRequestDto
import com.callover.android.core.data.auth.dto.RegisterRequestDto
import com.callover.android.core.data.auth.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): UserDto

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto,
    ): UserDto

    @POST("auth/logout")
    suspend fun logout()
}