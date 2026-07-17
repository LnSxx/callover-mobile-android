package com.callover.android.core.data.calls.mappers

import com.callover.android.core.data.calls.dto.PendingIceCandidateDto
import com.callover.android.core.domain.models.PendingIceCandidate

fun PendingIceCandidateDto.toDomainOrNull(): PendingIceCandidate? {
    if (fromUserId.isBlank() || sdp.isBlank()) {
        return null
    }

    return PendingIceCandidate(
        fromUserId = fromUserId,
        sdp = sdp,
        sdpMLineIndex = sdpMLineIndex,
        sdpMid = sdpMid,
    )
}