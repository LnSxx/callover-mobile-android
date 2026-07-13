package com.callover.android.core.domain.models

enum class CallType {
    Audio,
    Video,
}

fun CallType.toPayloadValue(): String {
    return when (this) {
        CallType.Audio -> "audio"
        CallType.Video -> "video"
    }
}