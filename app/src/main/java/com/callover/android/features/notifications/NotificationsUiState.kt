package com.callover.android.features.notifications

import com.callover.android.core.domain.models.Notification

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val includeArchived: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null,
)