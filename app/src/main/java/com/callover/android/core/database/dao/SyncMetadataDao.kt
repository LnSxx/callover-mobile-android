package com.callover.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.callover.android.core.database.entities.SyncMetadataEntity

@Dao
interface SyncMetadataDao {
    @Query("SELECT value FROM sync_metadata WHERE `key` = :key")
    suspend fun getValue(
        key: String,
    ): String?

    @Upsert
    suspend fun upsert(
        entity: SyncMetadataEntity,
    )

    @Query("DELETE FROM sync_metadata WHERE `key` = :key")
    suspend fun delete(
        key: String,
    )
}