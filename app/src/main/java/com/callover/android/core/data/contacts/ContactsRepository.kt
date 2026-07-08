package com.callover.android.core.data.contacts

import com.callover.android.core.domain.models.Contact
import com.callover.android.core.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ContactsRepository {
    fun observeIsSyncing(): StateFlow<Boolean>
    fun observeContacts(): Flow<List<Contact>>

    suspend fun syncContacts(): ApiResult<Unit>

    suspend fun createContact(
        alias: String,
        contactUserId: String,
    ): ApiResult<Contact>

    suspend fun deleteContact(
        id: String,
    ): ApiResult<Unit>

    suspend fun clearLocalContacts()
}