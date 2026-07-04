package com.callover.android.core.data.contacts.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    val id: String,
    val ownerId: String,
    val contactUserId: String,
    val alias: String? = null,
    val note: String? = null,
    val isFavourite: Boolean,
    val isBlocked: Boolean,
    val isMuted: Boolean,
    val createdAt: String,
    val updatedAt: String,
)