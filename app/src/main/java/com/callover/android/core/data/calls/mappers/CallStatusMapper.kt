package com.callover.android.core.data.calls.mappers

import com.callover.android.core.domain.models.CallStatus

fun String.toCallStatusOrNull(): CallStatus? {
    return when (this) {
        "calling" -> CallStatus.Calling
        "ringing" -> CallStatus.Ringing
        "active" -> CallStatus.Active
        else -> null
    }
}