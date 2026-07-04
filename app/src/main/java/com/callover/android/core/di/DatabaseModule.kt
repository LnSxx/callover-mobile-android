package com.callover.android.core.di

import android.content.Context
import androidx.room.Room
import com.callover.android.core.database.CalloverDatabase
import com.callover.android.core.database.dao.ContactsDao
import com.callover.android.core.database.dao.NotificationsDao
import com.callover.android.core.database.dao.SyncMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): CalloverDatabase {
        return Room.databaseBuilder(
            context,
            CalloverDatabase::class.java,
            "callover.db",
        ).build()
    }

    @Provides
    fun provideNotificationsDao(
        database: CalloverDatabase,
    ): NotificationsDao {
        return database.notificationsDao()
    }

    @Provides
    fun provideContactsDao(
        database: CalloverDatabase,
    ): ContactsDao {
        return database.contactsDao()
    }

    @Provides
    fun provideSyncMetadataDao(
        database: CalloverDatabase,
    ): SyncMetadataDao {
        return database.syncMetadataDao()
    }
}