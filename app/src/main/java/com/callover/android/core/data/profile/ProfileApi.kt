package com.callover.android.core.data.profile

import com.callover.android.core.data.auth.dto.UserDto
import retrofit2.http.GET

interface ProfileApi {
    @GET("profile/me")
    suspend fun getMe(): UserDto
}