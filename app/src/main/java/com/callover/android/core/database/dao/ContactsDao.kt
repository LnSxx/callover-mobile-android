package com.callover.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.callover.android.core.database.entities.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query(
        """
        SELECT *
        FROM contacts
        ORDER BY 
            CASE WHEN alias IS NULL OR alias = '' THEN contactUserId ELSE alias END COLLATE NOCASE ASC
        """
    )
    fun observeContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    fun observeContactById(
        id: String,
    ): Flow<ContactEntity?>

    @Upsert
    suspend fun upsertAll(
        contacts: List<ContactEntity>,
    )

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(
        id: String,
    )

    @Query("DELETE FROM contacts")
    suspend fun clearAll()
}