package com.callover.android.core.sync

import android.util.Log
import com.callover.android.core.call_coordinator.CallCoordinator
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.data.notifications.NotificationsRepository
import com.callover.android.core.realtime.RealtimeMessageRouter
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
    private val messageRouter: RealtimeMessageRouter,
    private val presenceRealtimeDataSource: PresenceRealtimeDataSource,
    private val presenceRepository: PresenceRepository,
    private val callCoordinator: CallCoordinator,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO,
    )

    private var sessionJob: Job? = null

    fun start() {
        if (sessionJob?.isActive == true) {
            return
        }

        Log.d(TAG, "start session sync")

        messageRouter.startListening()
        presenceRealtimeDataSource.startListeningConnectionEvents()

        sessionJob = scope.launch {
            launch {
                Log.d(TAG, "start collecting presence events")
                presenceRepository.collectPresenceEvents()
            }

            launch {
                Log.d(TAG, "start collecting signaling events")
                callCoordinator.collectSignalingEvents()
            }

            launch {
                Log.d(TAG, "start initial sync")
                contactsRepository.syncContacts()
                notificationsRepository.syncPendingReadMarks()
                notificationsRepository.refreshNotifications()
                callCoordinator.restoreCurrentRingingCallIfNeeded()
            }

            launch {
                Log.d(TAG, "start observing contacts for presence subscriptions")

                contactsRepository.observeContacts()
                    .map { contacts ->
                        contacts.map { contact ->
                            contact.contactUserId
                        }.sorted()
                    }
                    .distinctUntilChanged()
                    .collect { userIds ->
                        Log.d(TAG, "presence contacts userIds=$userIds")

                        presenceRealtimeDataSource.subscribeToUsers(
                            userIds = userIds,
                        )
                    }
            }
        }

        socketManager.connect()
    }

    fun stop() {
        sessionJob?.cancel()
        sessionJob = null

        presenceRepository.clear()

        presenceRealtimeDataSource.stopListeningConnectionEvents()
        messageRouter.stopListening()

        socketManager.disconnect()
    }

    fun syncNow() {
        scope.launch {
            contactsRepository.syncContacts()
            notificationsRepository.syncPendingReadMarks()
            notificationsRepository.refreshNotifications()
        }
    }

    companion object {
        private const val TAG = "SessionDataSync"
    }
}