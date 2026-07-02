package com.callover.android.core.data.profile

import com.callover.android.core.domain.models.User
import com.callover.android.core.network.ApiResult

interface ProfileRepository {
    suspend fun getMe(): ApiResult<User>
}