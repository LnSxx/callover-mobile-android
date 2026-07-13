package com.callover.android.core.realtime

import org.json.JSONObject

data class RealtimeMessage(
    val type: String,
    val payload: JSONObject,
)