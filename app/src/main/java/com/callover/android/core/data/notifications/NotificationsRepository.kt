package com.callover.android.core.data.notifications

import com.callover.android.core.domain.models.Notification
import com.callover.android.core.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    fun observeNotifications(
        includeArchived: Boolean,
    ): Flow<List<Notification>>

    fun observeUnreadCount(): Flow<Int>

    suspend fun syncNotifications(): ApiResult<Unit>

    suspend fun markAsRead(
        ids: List<String>,
    ): ApiResult<Unit>

    suspend fun syncPendingReadMarks(): ApiResult<Unit>

    suspend fun clearLocalNotifications()
}