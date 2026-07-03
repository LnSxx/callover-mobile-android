package com.callover.android.core.data.notifications

import com.callover.android.core.database.dao.NotificationsDao
import com.callover.android.core.domain.models.Notification
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val api: NotificationsApi,
    private val dao: NotificationsDao,
    private val json: Json,
) : NotificationsRepository {
    override fun observeNotifications(
        includeArchived: Boolean,
    ): Flow<List<Notification>> {
        val nowMillis = System.currentTimeMillis()

        val source = if (includeArchived) {
            dao.observeAllNotifications()
        } else {
            dao.observeActiveNotifications(nowMillis)
        }

        return source.map { entities ->
            entities.map { it.toDomain(nowMillis) }
        }
    }

    override fun observeUnreadCount(): Flow<Int> {
        return dao.observeActiveUnreadCount(
            nowMillis = System.currentTimeMillis(),
        )
    }

    override suspend fun syncNotifications(): ApiResult<Unit> {
        return safeApiCall(json) {
            var offset = 0
            val limit = 100

            do {
                val response = api.getNotifications(
                    limit = limit,
                    offset = offset,
                    status = null,
                )

                dao.upsertAll(
                    response.data.map { dto ->
                        dto.toEntity(json)
                    },
                )

                offset += response.pagination.count
            } while (
                response.pagination.next != null &&
                response.pagination.count > 0
            )
        }
    }

    override suspend fun markAsRead(
        ids: List<String>,
    ): ApiResult<Unit> {
        if (ids.isEmpty()) {
            return ApiResult.Success(Unit)
        }

        val nowIso = Instant.now().toString()

        dao.markAsReadLocally(
            ids = ids,
            readAt = nowIso,
            pendingReadSync = true,
        )

        val result = safeApiCall(json) {
            api.markAsRead(
                body = com.callover.android.core.data.notifications.dto.MarkNotificationsAsReadRequestDto(
                    notificationIds = ids,
                ),
            )
            dao.clearPendingReadSync(ids)
        }

        return result
    }

    override suspend fun syncPendingReadMarks(): ApiResult<Unit> {
        val ids = dao.getPendingReadSyncIds()

        if (ids.isEmpty()) {
            return ApiResult.Success(Unit)
        }

        return safeApiCall(json) {
            api.markAsRead(
                body = com.callover.android.core.data.notifications.dto.MarkNotificationsAsReadRequestDto(
                    notificationIds = ids,
                ),
            )
            dao.clearPendingReadSync(ids)
        }
    }

    override suspend fun clearLocalNotifications() {
        dao.clearAll()
    }
}