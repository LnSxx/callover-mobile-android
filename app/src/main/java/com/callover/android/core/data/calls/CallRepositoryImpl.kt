package com.callover.android.core.data.calls

import com.callover.android.core.data.calls.mappers.toDomainOrNull
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallsRepositoryImpl @Inject constructor(
    private val api: CallsApi,
    private val json: Json,
) : CallsRepository {
    override suspend fun getCurrentRingingCall(): ApiResult<CurrentRingingCallResult> {
        return safeApiCall(json) {
            val response = api.getCurrentRingingCall()

            CurrentRingingCallResult(
                call = response.call?.toDomainOrNull(),
                pendingIceCandidates = response.pendingIceCandidates
                    .mapNotNull { it.toDomainOrNull() },
            )
        }
    }
}