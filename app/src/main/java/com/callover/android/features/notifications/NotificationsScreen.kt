package com.callover.android.features.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.core.domain.models.Notification
import com.callover.android.core.domain.models.NotificationStatus
import com.callover.android.core.domain.models.NotificationType
import com.callover.android.features.notifications.components.NotificationListItem

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

            totalItems > 0 && lastVisibleItem.index >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            onLoadNextPage()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding(),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            FilterChip(
                selected = uiState.includeArchived,
                onClick = {
                    onIncludeArchivedChange(!uiState.includeArchived)
                },
                label = {
                    Text("Show archived notifications")
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isRefreshing && uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No notifications yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.notifications,
                            key = { notification -> notification.id },
                        ) { notification ->
                            NotificationListItem(
                                notification = notification,
                                onClick = {
                                    onNotificationClick(notification)
                                },
                            )
                        }

                        if (uiState.isLoadingNextPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }

        uiState.errorMessage?.let { errorMessage ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            ) {
                Text(errorMessage)
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