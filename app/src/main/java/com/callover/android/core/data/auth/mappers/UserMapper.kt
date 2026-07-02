package com.callover.android.core.data.auth.mappers

import com.callover.android.core.data.auth.dto.UserDto
import com.callover.android.core.domain.models.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        username = username,
    )
}