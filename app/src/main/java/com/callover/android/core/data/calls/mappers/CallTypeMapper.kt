package com.callover.android.core.data.calls.mappers

import com.callover.android.core.domain.models.CallType

fun String.toCallTypeOrNull(): CallType? {
    return when (this) {
        "audio" -> CallType.Audio
        "video" -> CallType.Video
        else -> null
    }
}