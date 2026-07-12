package com.callover.android.core.data.contacts.dto

import kotlinx.serialization.Serializable

@Serializable
data class EditContactRequestDto(
    val alias: String?,
    val note: String?,
    val isFavourite: Boolean?,
    val isBlocked: Boolean?,
    val isMuted: Boolean?,
)