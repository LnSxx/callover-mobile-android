package com.callover.android.core.data.notifications

import com.callover.android.core.data.notifications.dto.GetNotificationsResponseDto
import com.callover.android.core.data.notifications.dto.MarkNotificationsAsReadRequestDto
import com.callover.android.core.data.notifications.dto.MarkNotificationsAsReadResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface NotificationsApi {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("status") status: String? = null,
    ): GetNotificationsResponseDto

    @PATCH("notifications/read")
    suspend fun markAsRead(
        @Body body: MarkNotificationsAsReadRequestDto,
    ): MarkNotificationsAsReadResponseDto
}