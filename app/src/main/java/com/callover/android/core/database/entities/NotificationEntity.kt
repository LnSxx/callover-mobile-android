package com.callover.android.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,

    val userId: String,

    val type: String,
    val status: String,

    val title: String,
    val body: String?,

    val callId: String?,
    val callFromUserId: String?,
    val callFromUserName: String?,
    val callType: String?,

    val serviceCode: String?,
    val servicePayload: String?,

    val readAt: String?,
    val expiresAt: String,
    val createdAt: String,

    val expiresAtMillis: Long,
    val createdAtMillis: Long,

    val pendingReadSync: Boolean = false,
)