package com.callover.android.core.data.contacts

import com.callover.android.core.database.dao.ContactsDao
import com.callover.android.core.database.dao.SyncMetadataDao
import com.callover.android.core.database.entities.SyncMetadataEntity
import com.callover.android.core.domain.models.Contact
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val CONTACTS_LAST_REMOTE_UPDATED_AT = "contacts_last_remote_updated_at"

@Singleton
class ContactsRepositoryImpl @Inject constructor(
    private val api: ContactsApi,
    private val contactsDao: ContactsDao,
    private val syncMetadataDao: SyncMetadataDao,
    private val json: Json,
) : ContactsRepository {
    override fun observeContacts(): Flow<List<Contact>> {
        return contactsDao.observeContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncContacts(): ApiResult<Unit> {
        return safeApiCall(json) {
            val changedAfter = syncMetadataDao.getValue(
                key = CONTACTS_LAST_REMOTE_UPDATED_AT,
            )

            var cursor: String? = null
            var maxUpdatedAt: String? = changedAfter

            do {
                val response = api.getContacts(
                    limit = 100,
                    cursor = cursor,
                    changedAfter = changedAfter,
                )

                val entities = response.items.map { it.toEntity() }

                contactsDao.upsertAll(entities)

                val pageMaxUpdatedAt = response.items.maxOfOrNull { it.updatedAt }

                if (pageMaxUpdatedAt != null) {
                    maxUpdatedAt = maxOfNullableIso(
                        first = maxUpdatedAt,
                        second = pageMaxUpdatedAt,
                    )
                }

                cursor = response.nextCursor
            } while (cursor != null)

            maxUpdatedAt?.let { value ->
                syncMetadataDao.upsert(
                    SyncMetadataEntity(
                        key = CONTACTS_LAST_REMOTE_UPDATED_AT,
                        value = value,
                    ),
                )
            }
        }
    }

    override suspend fun deleteContact(
        id: String,
    ): ApiResult<Unit> {
        return safeApiCall(json) {
            api.deleteContact(id)
            contactsDao.deleteById(id)
        }
    }

    override suspend fun clearLocalContacts() {
        contactsDao.clearAll()
        syncMetadataDao.delete(CONTACTS_LAST_REMOTE_UPDATED_AT)
    }

    private fun maxOfNullableIso(
        first: String?,
        second: String?,
    ): String? {
        return when {
            first == null -> second
            second == null -> first
            first >= second -> first
            else -> second
        }
    }
}