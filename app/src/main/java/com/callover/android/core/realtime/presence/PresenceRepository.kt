package com.callover.android.core.realtime.presence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenceRepository @Inject constructor(
    private val presenceRealtimeDataSource: PresenceRealtimeDataSource,
) {
    private val _onlineUserIds = MutableStateFlow<Set<String>>(emptySet())
    val onlineUserIds: StateFlow<Set<String>> = _onlineUserIds.asStateFlow()

    suspend fun collectPresenceEvents() {
        presenceRealtimeDataSource.events.collect { event ->
            when (event) {
                is PresenceEvent.Initial -> {
                    _onlineUserIds.value = event.onlineUserIds
                }

                is PresenceEvent.UserOnline -> {
                    _onlineUserIds.update { current ->
                        current + event.userId
                    }
                }

                is PresenceEvent.UserOffline -> {
                    _onlineUserIds.update { current ->
                        current - event.userId
                    }
                }
            }
        }
    }

    fun observeIsOnline(userId: String): Flow<Boolean> {
        return onlineUserIds.map { onlineIds ->
            userId in onlineIds
        }
    }

    fun clear() {
        _onlineUserIds.value = emptySet()
    }
}