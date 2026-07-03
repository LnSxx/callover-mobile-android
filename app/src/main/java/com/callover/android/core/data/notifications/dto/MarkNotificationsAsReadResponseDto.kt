package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable

@Serializable
data class MarkNotificationsAsReadResponseDto(
    val unreadRemain: Int,
)