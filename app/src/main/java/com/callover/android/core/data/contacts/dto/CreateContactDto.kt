package com.callover.android.core.data.contacts.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateContactDto(
    val contactUserId: String,
    val alias: String,
)