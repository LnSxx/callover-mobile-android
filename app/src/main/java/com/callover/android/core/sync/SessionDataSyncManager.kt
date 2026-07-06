package com.callover.android.core.sync

import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.data.notifications.NotificationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionDataSyncManager @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val notificationsRepository: NotificationsRepository,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO,
    )

    private var syncJob: Job? = null

    fun start() {
        if (syncJob?.isActive == true) {
            return
        }

        syncJob = scope.launch {
            contactsRepository.syncContacts()
            notificationsRepository.syncPendingReadMarks()
            notificationsRepository.refreshNotifications()
        }
    }

    fun stop() {
        syncJob?.cancel()
        syncJob = null
    }

    fun syncNow() {
        scope.launch {
            contactsRepository.syncContacts()
            notificationsRepository.syncPendingReadMarks()
            notificationsRepository.refreshNotifications()
        }
    }
}