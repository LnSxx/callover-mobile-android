package com.callover.android.core.data.contacts

import com.callover.android.core.data.contacts.dto.ContactDto
import com.callover.android.core.database.entities.ContactEntity
import com.callover.android.core.domain.models.Contact
import java.time.Instant

fun ContactDto.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        ownerId = ownerId,
        contactUserId = contactUserId,
        alias = alias,
        note = note,
        isFavourite = isFavourite,
        isBlocked = isBlocked,
        isMuted = isMuted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdAtMillis = createdAt.toEpochMillis(),
        updatedAtMillis = updatedAt.toEpochMillis(),
    )
}

fun ContactEntity.toDomain(): Contact {
    return Contact(
        id = id,
        ownerId = ownerId,
        contactUserId = contactUserId,
        alias = alias,
        note = note,
        isFavourite = isFavourite,
        isBlocked = isBlocked,
        isMuted = isMuted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

private fun String.toEpochMillis(): Long {
    return runCatching {
        Instant.parse(this).toEpochMilli()
    }.getOrDefault(0L)
}