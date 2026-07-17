package com.callover.android.core.data.calls.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetCurrentRingingCallResponseDto(
    val call: CallDto?,
    val pendingIceCandidates: List<PendingIceCandidateDto> = emptyList(),
)