package com.callover.android.core.data.notifications.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPaginationDto(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int,
    val next: String?,
    val previous: String?,
)