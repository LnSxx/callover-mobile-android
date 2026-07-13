package com.callover.android.core.signaling

import com.callover.android.core.domain.models.CallType
import com.callover.android.core.network.ApiResult

interface SignalingService {
    suspend fun sendOffer(
        toUserId: String,
        sdp: String,
        type: CallType,
    ): ApiResult<Unit>

    suspend fun sendAnswer(
        toUserId: String,
        sdp: String,
    ): ApiResult<Unit>

    suspend fun sendDecline(
        toUserId: String,
    ): ApiResult<Unit>

    suspend fun sendCancel(
        toUserId: String,
    ): ApiResult<Unit>

    suspend fun sendEnd(
        toUserId: String,
    ): ApiResult<Unit>

    suspend fun sendIceCandidate(
        toUserId: String,
        sdp: String,
        sdpMLineIndex: Int,
        sdpMid: String?,
    ): ApiResult<Unit>
}