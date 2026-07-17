package com.callover.android.core.data.calls

import com.callover.android.core.domain.models.Call
import com.callover.android.core.domain.models.PendingIceCandidate
import com.callover.android.core.network.ApiResult

data class CurrentRingingCallResult(
    val call: Call?,
    val pendingIceCandidates: List<PendingIceCandidate>,
)

interface CallsRepository {
    suspend fun getCurrentRingingCall(): ApiResult<CurrentRingingCallResult>
}