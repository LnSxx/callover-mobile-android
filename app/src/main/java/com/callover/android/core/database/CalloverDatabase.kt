package com.callover.android.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.callover.android.core.database.dao.NotificationsDao
import com.callover.android.core.database.entities.NotificationEntity

@Database(
    entities = [
        NotificationEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CalloverDatabase : RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao
}