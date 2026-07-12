package com.callover.android.core.data.contacts

import com.callover.android.core.data.contacts.dto.ContactDto
import com.callover.android.core.data.contacts.dto.CreateContactDto
import com.callover.android.core.data.contacts.dto.EditContactRequestDto
import com.callover.android.core.data.contacts.dto.GetContactsResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactsApi {
    @POST("contacts")
    suspend fun createContact(
        @Body body: CreateContactDto,
    ): ContactDto
    @GET("contacts")
    suspend fun getContacts(
        @Query("limit") limit: Int = 100,
        @Query("cursor") cursor: String? = null,
        @Query("changedAfter") changedAfter: String? = null,
    ): GetContactsResponseDto

    @PATCH("contacts/{id}")
    suspend fun editContact(
        @Path("id") id: String,
        @Body body: EditContactRequestDto,
    ): ContactDto

    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Path("id") id: String,
    )
}