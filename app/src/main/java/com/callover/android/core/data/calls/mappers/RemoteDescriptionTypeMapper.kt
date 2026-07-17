package com.callover.android.core.data.calls.mappers

import com.callover.android.core.domain.models.RemoteDescriptionType

fun String.toRemoteDescriptionTypeOrNull(): RemoteDescriptionType? {
    return when (this) {
        "offer" -> RemoteDescriptionType.Offer
        "answer" -> RemoteDescriptionType.Answer
        else -> null
    }
}