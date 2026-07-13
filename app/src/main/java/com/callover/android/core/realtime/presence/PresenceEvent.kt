package com.callover.android.core.realtime.presence

sealed interface PresenceEvent {
    data class Initial(
        val onlineUserIds: Set<String>,
    ) : PresenceEvent

    data class UserOnline(
        val userId: String,
    ) : PresenceEvent

    data class UserOffline(
        val userId: String,
    ) : PresenceEvent
}