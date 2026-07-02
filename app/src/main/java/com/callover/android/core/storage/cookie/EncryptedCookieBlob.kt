package com.callover.android.core.storage.cookie

import kotlinx.serialization.Serializable

@Serializable
data class EncryptedCookieBlob(
    val cipherText: String,
    val iv: String,
)