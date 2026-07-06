package com.callover.android.core.data.contacts

import com.callover.android.core.database.dao.ContactsDao
import com.callover.android.core.database.dao.SyncMetadataDao
import com.callover.android.core.database.entities.SyncMetadataEntity
import com.callover.android.core.domain.models.Contact
import com.callover.android.core.network.ApiResult
import com.callover.android.core.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val _isSyncing = MutableStateFlow(false)
    private val syncMutex = Mutex()

    override fun observeIsSyncing(): StateFlow<Boolean> {
        return _isSyncing.asStateFlow()
    }

    override fun observeContacts(): Flow<List<Contact>> {
        return contactsDao.observeContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncContacts(): ApiResult<Unit> {
        if (syncMutex.isLocked) {
            return ApiResult.Success(Unit)
        }

        return syncMutex.withLock {
            _isSyncing.value = true

            try {
                safeApiCall(json) {
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

                        contactsDao.upsertAll(
                            contacts = response.items.map { contactDto ->
                                contactDto.toEntity()
                            },
                        )

                        val pageMaxUpdatedAt = response.items
                            .maxOfOrNull { contactDto -> contactDto.updatedAt }

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

                    Unit
                }
            } finally {
                _isSyncing.value = false
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