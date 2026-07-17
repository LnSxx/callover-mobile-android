package com.callover.android.core.data.calls.mappers

import com.callover.android.core.data.calls.dto.RemoteDescriptionDto
import com.callover.android.core.domain.models.RemoteDescription

fun RemoteDescriptionDto.toDomainOrNull(): RemoteDescription? {
    val descriptionType = type.toRemoteDescriptionTypeOrNull()
        ?: return null

    if (sdp.isBlank()) {
        return null
    }

    return RemoteDescription(
        type = descriptionType,
        sdp = sdp,
    )
}