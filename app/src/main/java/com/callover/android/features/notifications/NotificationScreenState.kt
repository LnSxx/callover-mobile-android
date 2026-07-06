package com.callover.android.features.notifications

data class NotificationsScreenState(
    val isRefreshing: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null,
)