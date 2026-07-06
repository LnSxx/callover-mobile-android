package com.callover.android.core.domain.models

data class Contact(
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
)