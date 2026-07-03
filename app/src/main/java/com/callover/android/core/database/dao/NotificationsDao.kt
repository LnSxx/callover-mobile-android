package com.callover.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.callover.android.core.database.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {
    @Query(
        """
        SELECT *
        FROM notifications
        WHERE expiresAtMillis > :nowMillis
        ORDER BY createdAtMillis DESC
        """
    )
    fun observeActiveNotifications(
        nowMillis: Long,
    ): Flow<List<NotificationEntity>>

    @Query(
        """
        SELECT *
        FROM notifications
        ORDER BY createdAtMillis DESC
        """
    )
    fun observeAllNotifications(): Flow<List<NotificationEntity>>

    @Query(
        """
        SELECT COUNT(*)
        FROM notifications
        WHERE status = 'unread'
        AND expiresAtMillis > :nowMillis
        """
    )
    fun observeActiveUnreadCount(
        nowMillis: Long,
    ): Flow<Int>

    @Upsert
    suspend fun upsertAll(
        notifications: List<NotificationEntity>,
    )

    @Query(
        """
        UPDATE notifications
        SET status = 'read',
            readAt = :readAt,
            pendingReadSync = :pendingReadSync
        WHERE id IN (:ids)
        """
    )
    suspend fun markAsReadLocally(
        ids: List<String>,
        readAt: String,
        pendingReadSync: Boolean,
    )

    @Query(
        """
        SELECT id
        FROM notifications
        WHERE pendingReadSync = 1
        """
    )
    suspend fun getPendingReadSyncIds(): List<String>

    @Query(
        """
        UPDATE notifications
        SET pendingReadSync = 0
        WHERE id IN (:ids)
        """
    )
    suspend fun clearPendingReadSync(
        ids: List<String>,
    )

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}