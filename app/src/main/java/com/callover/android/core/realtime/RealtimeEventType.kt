package com.callover.android.core.realtime

enum class RealtimeEventType(val type: String) {
    PresenceSubscribe("presence.subscribe"),
    PresenceInitial("presence.initial"),
    PresenceUserOnline("presence.user.online"),
    PresenceUserOffline("presence.user.offline"),

    CallOffer("call.offer"),
    CallAnswer("call.answer"),
    CallDecline("call.decline"),
    CallCancel("call.cancel"),
    CallEnd("call.end"),
    CallTimeout("call.timeout"),
    CallIceCandidate("call.ice-candidate"),
}