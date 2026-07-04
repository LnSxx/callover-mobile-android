package com.callover.android.core.data.contacts

import com.callover.android.core.domain.models.Contact
import com.callover.android.core.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun observeContacts(): Flow<List<Contact>>

    suspend fun syncContacts(): ApiResult<Unit>

    suspend fun deleteContact(
        id: String,
    ): ApiResult<Unit>

    suspend fun clearLocalContacts()
}