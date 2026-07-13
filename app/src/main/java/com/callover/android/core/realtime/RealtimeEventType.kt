package com.callover.android.core.realtime

enum class RealtimeEventType(val type: String) {
    PresenceSubscribe("presence.subscribe"),
    PresenceInitial("presence.initial"),
    PresenceUserOnline("presence.user.online"),
    PresenceUserOffline("presence.user.offline"),
}