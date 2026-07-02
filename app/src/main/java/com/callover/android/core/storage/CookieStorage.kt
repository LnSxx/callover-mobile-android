package com.callover.android.core.storage

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.callover.android.core.network.StoredCookie
import com.callover.android.core.security.EncryptedPayload
import com.callover.android.core.security.KeystoreCrypto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cookieDataStore by preferencesDataStore(
    name = "callover_cookie_store",
)

@Singleton
class CookieStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val keystoreCrypto: KeystoreCrypto,
) {
    fun loadCookies(): List<StoredCookie> {
        return runBlocking {
            val encryptedBlobJson = context.cookieDataStore.data
                .first()[COOKIES_KEY]
                ?: return@runBlocking emptyList()

            runCatching {
                val encryptedBlob = json.decodeFromString<EncryptedCookieBlob>(
                    encryptedBlobJson,
                )

                val payload = EncryptedPayload(
                    cipherText = encryptedBlob.cipherText.decodeBase64(),
                    iv = encryptedBlob.iv.decodeBase64(),
                )

                val decryptedBytes = keystoreCrypto.decrypt(payload)
                val cookiesJson = decryptedBytes.toString(StandardCharsets.UTF_8)

                json.decodeFromString<List<StoredCookie>>(cookiesJson)
            }.getOrDefault(emptyList())
        }
    }

    fun saveCookies(cookies: List<StoredCookie>) {
        runBlocking {
            val cookiesJson = json.encodeToString(cookies)
            val encryptedPayload = keystoreCrypto.encrypt(
                cookiesJson.toByteArray(StandardCharsets.UTF_8),
            )

            val encryptedBlob = EncryptedCookieBlob(
                cipherText = encryptedPayload.cipherText.encodeBase64(),
                iv = encryptedPayload.iv.encodeBase64(),
            )

            context.cookieDataStore.edit { preferences ->
                preferences[COOKIES_KEY] = json.encodeToString(encryptedBlob)
            }
        }
    }

    fun clearCookies() {
        runBlocking {
            context.cookieDataStore.edit { preferences ->
                preferences.remove(COOKIES_KEY)
            }
        }
    }

    private fun ByteArray.encodeBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }

    private fun String.decodeBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }

    private companion object {
        val COOKIES_KEY = stringPreferencesKey("encrypted_cookies")
    }
}