package com.callover.android.core.data.notifications

import com.callover.android.core.data.notifications.dto.NotificationDto
import com.callover.android.core.database.entities.NotificationEntity
import com.callover.android.core.domain.models.Notification
import com.callover.android.core.domain.models.NotificationCall
import com.callover.android.core.domain.models.NotificationService
import com.callover.android.core.domain.models.NotificationStatus
import com.callover.android.core.domain.models.NotificationType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

fun NotificationDto.toEntity(
    json: Json,
): NotificationEntity {
    return NotificationEntity(
        id = id,
        userId = userId,
        type = type,
        status = status,
        title = title,
        body = body,
        callId = call?.callId,
        callFromUserId = call?.fromUserId,
        callFromUserName = call?.fromUserName,
        callType = call?.callType,
        serviceCode = service?.code,
        servicePayload = service?.payload?.let { json.encodeToString(it) },
        readAt = readAt,
        expiresAt = expiresAt,
        createdAt = createdAt,
        expiresAtMillis = expiresAt.toEpochMillis(),
        createdAtMillis = createdAt.toEpochMillis(),
        pendingReadSync = false,
    )
}

fun NotificationEntity.toDomain(
    nowMillis: Long,
): Notification {
    return Notification(
        id = id,
        userId = userId,
        type = type.toNotificationType(),
        status = status.toNotificationStatus(),
        title = title,
        body = body,
        call = if (callId != null && callFromUserId != null && callType != null) {
            NotificationCall(
                callId = callId,
                fromUserId = callFromUserId,
                fromUserName = callFromUserName,
                callType = callType,
            )
        } else {
            null
        },
        service = if (serviceCode != null || servicePayload != null) {
            NotificationService(
                code = serviceCode,
                payload = servicePayload,
            )
        } else {
            null
        },
        readAt = readAt,
        expiresAt = expiresAt,
        createdAt = createdAt,
        isArchived = expiresAtMillis <= nowMillis,
    )
}

private fun String.toNotificationType(): NotificationType {
    return when (this) {
        "missed_call" -> NotificationType.MissedCall
        "muted_call" -> NotificationType.MutedCall
        "service_message" -> NotificationType.ServiceMessage
        else -> NotificationType.Unknown
    }
}

private fun String.toNotificationStatus(): NotificationStatus {
    return when (this) {
        "read" -> NotificationStatus.Read
        else -> NotificationStatus.Unread
    }
}

private fun String.toEpochMillis(): Long {
    return runCatching {
        Instant.parse(this).toEpochMilli()
    }.getOrDefault(0L)
}