package com.callover.android.core.storage.user

import com.callover.android.core.domain.models.User
import kotlinx.serialization.Serializable

@Serializable
data class StoredUser(
    val id: String,
    val username: String,
)

fun User.toStoredUser(): StoredUser {
    return StoredUser(
        id = id,
        username = username,
    )
}

fun StoredUser.toDomain(): User {
    return User(
        id = id,
        username = username,
    )
}