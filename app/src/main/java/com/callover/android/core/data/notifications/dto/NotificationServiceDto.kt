package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationServiceDto(
    val code: String? = null,
    val payload: String? = null,
)