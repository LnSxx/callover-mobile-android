package com.callover.android.core.data.contacts.dto


import kotlinx.serialization.Serializable

@Serializable
data class GetContactsResponseDto(
    val items: List<ContactDto>,
    val nextCursor: String? = null,
)