package com.callover.android.core.data.notifications.dto

@Serializable
data class NotificationDto(
    val id: String,
    val userId: String,
    val type: String,
    val status: String,
    val title: String,
    val body: String? = null,
    val call: NotificationCallDto? = null,
    val service: NotificationServiceDto? = null,
    val readAt: String? = null,
    val expiresAt: String,
    val createdAt: String,
)