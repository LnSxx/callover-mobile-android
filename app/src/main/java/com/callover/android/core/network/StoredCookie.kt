package com.callover.android.core.network

import kotlinx.serialization.Serializable
import okhttp3.Cookie

@Serializable
data class StoredCookie(
    val name: String,
    val value: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val persistent: Boolean,
    val hostOnly: Boolean,
)

fun Cookie.toStoredCookie(): StoredCookie {
    return StoredCookie(
        name = name,
        value = value,
        expiresAt = expiresAt,
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly,
        persistent = persistent,
        hostOnly = hostOnly,
    )
}

fun StoredCookie.toOkHttpCookie(): Cookie {
    val builder = Cookie.Builder()
        .name(name)
        .value(value)
        .expiresAt(expiresAt)
        .path(path)

    if (hostOnly) {
        builder.hostOnlyDomain(domain)
    } else {
        builder.domain(domain)
    }

    if (secure) {
        builder.secure()
    }

    if (httpOnly) {
        builder.httpOnly()
    }

    return builder.build()
}