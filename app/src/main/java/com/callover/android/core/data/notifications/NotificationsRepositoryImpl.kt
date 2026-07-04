package com.callover.android.core.data.notifications

import com.callover.android.core.data.notifications.dto.MarkNotificationsAsReadRequestDto
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
    private var nextOffset: Int? = 0
    private val pageLimit = 30

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
            entities.map { entity ->
                entity.toDomain(nowMillis)
            }
        }
    }

    override fun observeUnreadCount(): Flow<Int> {
        return dao.observeActiveUnreadCount(
            nowMillis = System.currentTimeMillis(),
        )
    }

    override suspend fun refreshNotifications(): ApiResult<Unit> {
        nextOffset = 0

        return loadPage(
            offset = 0,
            replacePagingState = true,
        )
    }

    override suspend fun loadNextNotificationsPage(): ApiResult<Unit> {
        val offset = nextOffset ?: return ApiResult.Success(Unit)

        return loadPage(
            offset = offset,
            replacePagingState = true,
        )
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

        return safeApiCall(json) {
            api.markAsRead(
                body = MarkNotificationsAsReadRequestDto(
                    notificationIds = ids,
                ),
            )

            dao.clearPendingReadSync(ids)
        }
    }

    override suspend fun syncPendingReadMarks(): ApiResult<Unit> {
        val ids = dao.getPendingReadSyncIds()

        if (ids.isEmpty()) {
            return ApiResult.Success(Unit)
        }

        return safeApiCall(json) {
            api.markAsRead(
                body = MarkNotificationsAsReadRequestDto(
                    notificationIds = ids,
                ),
            )

            dao.clearPendingReadSync(ids)
        }
    }

    override suspend fun clearLocalNotifications() {
        dao.clearAll()
        nextOffset = 0
    }

    private suspend fun loadPage(
        offset: Int,
        replacePagingState: Boolean,
    ): ApiResult<Unit> {
        return safeApiCall(json) {
            val response = api.getNotifications(
                limit = pageLimit,
                offset = offset,
                status = null,
            )

            dao.upsertAll(
                notifications = response.data.map { dto ->
                    dto.toEntity(json)
                },
            )

            if (replacePagingState) {
                nextOffset = if (response.pagination.next != null) {
                    response.pagination.offset + response.pagination.count
                } else {
                    null
                }
            }
        }
    }
}