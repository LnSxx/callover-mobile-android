package com.callover.android.features.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.core.domain.models.Notification
import com.callover.android.core.domain.models.NotificationStatus
import com.callover.android.core.domain.models.NotificationType
import com.callover.android.features.notifications.componenets.EmptyNotificationList
import com.callover.android.features.notifications.componenets.NotificationsListContent

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onLoadNextPage = viewModel::loadNextPage,
        onIncludeArchivedChange = viewModel::setIncludeArchived,
        onNotificationClick = { notification ->
            if (notification.status == NotificationStatus.Unread) {
                viewModel.markAsRead(notification.id)
            }
        },
    )
}

@Composable
private fun NotificationsScreenContent(
    uiState: NotificationsUiState,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onIncludeArchivedChange: (Boolean) -> Unit,
    onNotificationClick: (Notification) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    val shouldLoadNextPage by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            val totalItems = listState.layoutInfo.totalItemsCount

            uiState.hasLoaded &&
                    uiState.notifications.isNotEmpty() &&
                    !uiState.isRefreshing &&
                    !uiState.isLoadingNextPage &&
                    totalItems > 0 &&
                    lastVisibleItem.index >= totalItems - 1
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            onLoadNextPage()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 24.dp,
        ),
    ) {
        item(
            key = "header",
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.notifications_title),
                    style = MaterialTheme.typography.headlineLarge,
                )

                if (uiState.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }

        item(
            key = "top-space",
        ) {
            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            !uiState.hasLoaded -> {
                // render nothing
            }

            uiState.notifications.isEmpty() -> {
                item(
                    key = "empty-notifications",
                ) {
                    EmptyNotificationList()
                }
            }

            else -> {
                NotificationsListContent(
                    notifications = uiState.notifications,
                    onNotificationClick = onNotificationClick,
                )
            }
        }

        if (uiState.isLoadingNextPage) {
            item(
                key = "next-page-loader",
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Notifications Screen Preview",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
private fun NotificationsScreenPreview() {
    NotificationsScreenContent(
        uiState = NotificationsUiState(
            notifications = listOf(
                Notification(
                    id = "1",
                    userId = "user-1",
                    type = NotificationType.MissedCall,
                    status = NotificationStatus.Unread,
                    title = "Missed call",
                    body = "You missed a call from Alice.",
                    call = null,
                    service = null,
                    readAt = null,
                    expiresAt = "2026-08-01T00:00:00.000Z",
                    createdAt = "2026-07-03T00:00:00.000Z",
                    isArchived = false,
                ),
                Notification(
                    id = "2",
                    userId = "user-1",
                    type = NotificationType.ServiceMessage,
                    status = NotificationStatus.Read,
                    title = "Welcome to Callover",
                    body = "Your account is ready.",
                    call = null,
                    service = null,
                    readAt = "2026-07-03T00:00:00.000Z",
                    expiresAt = "2026-08-01T00:00:00.000Z",
                    createdAt = "2026-07-03T00:00:00.000Z",
                    isArchived = false,
                ),
            ),
            unreadCount = 1,
            includeArchived = false,
        ),
        onRefresh = {},
        onLoadNextPage = {},
        onIncludeArchivedChange = {},
        onNotificationClick = {},
        modifier = Modifier.fillMaxSize(),
    )
}