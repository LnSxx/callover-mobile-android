package com.callover.android.core.realtime.signaling.mappers

import com.callover.android.core.domain.models.CallTimeoutReason

fun String.toCallTimeoutReasonOrNull(): CallTimeoutReason? {
    return when (this) {
        "no_answer" -> CallTimeoutReason.NoAnswer
        "max_duration" -> CallTimeoutReason.MaxDuration
        else -> null
    }
}