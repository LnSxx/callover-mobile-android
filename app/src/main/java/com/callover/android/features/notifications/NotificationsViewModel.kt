package com.callover.android.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callover.android.core.data.notifications.NotificationsRepository
import com.callover.android.core.network.ApiError
import com.callover.android.core.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {
    private val includeArchived = MutableStateFlow(false)
    private val screenState = MutableStateFlow(NotificationsScreenState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        includeArchived.flatMapLatest { includeArchived ->
            notificationsRepository.observeNotifications(includeArchived)
        },
        notificationsRepository.observeUnreadCount(),
        includeArchived,
        screenState,
    ) { notifications, unreadCount, includeArchived, screenState ->
        NotificationsUiState(
            notifications = notifications,
            unreadCount = unreadCount,
            includeArchived = includeArchived,
            isRefreshing = screenState.isRefreshing,
            isLoadingNextPage = screenState.isLoadingNextPage,
            hasLoaded = screenState.hasLoaded,
            errorMessage = screenState.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotificationsUiState(
            notifications = emptyList(),
            unreadCount = 0,
            includeArchived = false,
            isRefreshing = true,
            isLoadingNextPage = false,
            hasLoaded = false,
            errorMessage = null,
        ),
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            screenState.update {
                it.copy(
                    isRefreshing = true,
                    errorMessage = null,
                )
            }

            notificationsRepository.syncPendingReadMarks()

            val result = notificationsRepository.refreshNotifications()

            screenState.update {
                it.copy(
                    isRefreshing = false,
                    hasLoaded = true,
                    errorMessage = result.errorMessageOrNull(),
                )
            }
        }
    }

    fun loadNextPage() {
        val currentState = screenState.value

        if (currentState.isLoadingNextPage || currentState.isRefreshing) {
            return
        }

        viewModelScope.launch {
            screenState.update {
                it.copy(
                    isLoadingNextPage = true,
                    errorMessage = null,
                )
            }

            val result = notificationsRepository.loadNextNotificationsPage()

            screenState.update {
                it.copy(
                    isLoadingNextPage = false,
                    errorMessage = result.errorMessageOrNull(),
                )
            }
        }
    }

    fun setIncludeArchived(value: Boolean) {
        includeArchived.value = value
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationsRepository.markAsRead(
                ids = listOf(notificationId),
            )
        }
    }

    fun clearError() {
        screenState.update {
            it.copy(errorMessage = null)
        }
    }
}

private fun ApiResult<Unit>.errorMessageOrNull(): String? {
    return when (this) {
        is ApiResult.Success -> null

        is ApiResult.Error -> {
            when (error) {
                ApiError.Network -> "Network error. Showing saved notifications."
                ApiError.Unknown -> "Something went wrong."
                ApiError.Unauthorized -> null

                is ApiError.Backend -> error.message
            }
        }
    }
}