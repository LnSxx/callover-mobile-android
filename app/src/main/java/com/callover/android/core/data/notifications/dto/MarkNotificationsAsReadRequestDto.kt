package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializer

@Serializer
data class MarkNotificationsAsReadRequestDto(
    val notificationIds: List<String>,
)