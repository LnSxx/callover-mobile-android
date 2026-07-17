package com.callover.android.core.data.calls.mappers

import com.callover.android.core.data.calls.dto.CallDto
import com.callover.android.core.domain.models.Call

fun CallDto.toDomainOrNull(): Call? {
    val callType = type.toCallTypeOrNull()
    val callStatus = status.toCallStatusOrNull()

    if (callType == null || callStatus == null) {
        return null
    }

    return Call(
        type = callType,
        userId = userId,
        peerUserId = peerUserId,
        roomId = roomId,
        status = callStatus,
        createdAt = createdAt,
        acceptedAt = acceptedAt,
        remoteDescription = remoteDescription?.toDomainOrNull(),
    )
}



