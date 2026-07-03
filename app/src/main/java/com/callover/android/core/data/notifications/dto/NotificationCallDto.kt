package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationCallDto(
    val callId: String,
    val fromUserId: String,
    val fromUserName: String? = null,
    val callType: String,
)