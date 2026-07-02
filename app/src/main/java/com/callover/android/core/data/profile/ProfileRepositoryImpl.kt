package com.callover.android.core.data.profile

import com.callover.android.core.data.auth.mappers.toDomain
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import com.callover.android.core.domain.models.User
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val json: Json,
) : ProfileRepository {
    override suspend fun getMe(): ApiResult<User> {
        return safeApiCall(json) {
            profileApi.getMe().toDomain()
        }
    }
}