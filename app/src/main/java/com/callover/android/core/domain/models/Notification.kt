package com.callover.android.core.domain.models

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val title: String,
    val body: String?,
    val call: NotificationCall?,
    val service: NotificationService?,
    val readAt: String?,
    val expiresAt: String,
    val createdAt: String,
    val isArchived: Boolean,
)

enum class NotificationType {
    MissedCall,
    MutedCall,
    ServiceMessage,
    Unknown,
}

enum class NotificationStatus {
    Unread,
    Read,
}

data class NotificationCall(
    val callId: String,
    val fromUserId: String,
    val fromUserName: String?,
    val callType: String,
)

data class NotificationService(
    val code: String?,
    val payload: String?,
)