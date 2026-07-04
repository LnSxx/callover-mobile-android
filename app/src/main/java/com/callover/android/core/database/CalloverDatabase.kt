package com.callover.android.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.callover.android.core.database.dao.ContactsDao
import com.callover.android.core.database.dao.NotificationsDao
import com.callover.android.core.database.dao.SyncMetadataDao
import com.callover.android.core.database.entities.NotificationEntity
import com.callover.android.core.database.entities.ContactEntity
import com.callover.android.core.database.entities.SyncMetadataEntity

@Database(
    entities = [
        NotificationEntity::class,
        ContactEntity::class,
        SyncMetadataEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class CalloverDatabase : RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao
    abstract fun contactsDao(): ContactsDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}