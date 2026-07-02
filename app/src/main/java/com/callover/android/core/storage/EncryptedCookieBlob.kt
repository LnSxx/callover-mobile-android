package com.callover.android.core.storage

import kotlinx.serialization.Serializable

@Serializable
data class EncryptedCookieBlob(
    val cipherText: String,
    val iv: String,
)