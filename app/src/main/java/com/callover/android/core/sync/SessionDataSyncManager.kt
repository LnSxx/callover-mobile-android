package com.callover.android.core.sync

import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.data.notifications.NotificationsRepository
import com.callover.android.core.realtime.SocketManager
import com.callover.android.core.realtime.presence.PresenceRealtimeDataSource
import com.callover.android.core.realtime.presence.PresenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionDataSyncManager @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val socketManager: SocketManager,
    private val presenceRealtimeDataSource: PresenceRealtimeDataSource,
    private val presenceRepository: PresenceRepository,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO,
    )

    private var sessionJob: Job? = null

    fun start() {
        if (sessionJob?.isActive == true) {
            return
        }

        socketManager.connect()
        presenceRealtimeDataSource.startListening()

        sessionJob = scope.launch {
            launch {
                presenceRepository.collectPresenceEvents()
            }

            launch {
                contactsRepository.syncContacts()
                notificationsRepository.syncPendingReadMarks()
                notificationsRepository.refreshNotifications()
            }

            launch {
                // TODO: Implement algorithm of defining contact of user's interest (5-10)
                //  and initially subscribe only to them. For now subscribe to all user contacts.
                contactsRepository.observeContacts()
                    .map { contacts ->
                        contacts.map { contact ->
                            contact.contactUserId
                        }.sorted()
                    }
                    .distinctUntilChanged()
                    .collect { userIds ->
                        presenceRealtimeDataSource.subscribeToUsers(
                            userIds = userIds,
                        )
                    }
            }
        }
    }

    fun stop() {
        sessionJob?.cancel()
        sessionJob = null

        presenceRepository.clear()
        presenceRealtimeDataSource.stopListening()
        socketManager.disconnect()
    }

    fun syncNow() {
        scope.launch {
            contactsRepository.syncContacts()
            notificationsRepository.syncPendingReadMarks()
            notificationsRepository.refreshNotifications()
        }
    }
}