package com.callover.android.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,

    val ownerId: String,
    val contactUserId: String,

    val alias: String,
    val note: String?,

    val isFavourite: Boolean,
    val isBlocked: Boolean,
    val isMuted: Boolean,

    val createdAt: String,
    val updatedAt: String,

    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)