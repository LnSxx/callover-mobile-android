package com.callover.android.core.data.calls

import com.callover.android.core.data.calls.dto.GetCurrentRingingCallResponseDto
import retrofit2.http.GET

interface CallsApi {
    @GET("calls/current")
    suspend fun getCurrentRingingCall(): GetCurrentRingingCallResponseDto
}