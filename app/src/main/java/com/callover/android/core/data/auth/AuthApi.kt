package com.callover.android.core.data.auth

import com.callover.android.core.data.auth.dto.LoginRequestDto
import com.callover.android.core.data.auth.dto.LoginResponseDto
import com.callover.android.core.data.auth.dto.RegisterRequestDto
import com.callover.android.core.data.auth.dto.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): LoginResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto,
    ): RegisterResponseDto

    @POST("auth/logout")
    suspend fun logout()
}