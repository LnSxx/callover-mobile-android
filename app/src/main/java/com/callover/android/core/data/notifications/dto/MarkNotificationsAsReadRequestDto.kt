package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable


@Serializable
data class MarkNotificationsAsReadRequestDto(
    val notificationIds: List<String>,
)