package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetNotificationsResponseDto(
    val data: List<NotificationDto>,
    val totalUnreadCount: Int,
    val pagination: NotificationPaginationDto,
)