package com.callover.android.core.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.callover.android.core.network.StoredCookie
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cookieDataStore by preferencesDataStore(
    name = "callover_cookies",
)

@Singleton
class CookieStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    private val cookiesKey = stringPreferencesKey("cookies")

    fun loadCookies(): List<StoredCookie> {
        return runBlocking {
            val rawCookies = context.cookieDataStore.data.first()[cookiesKey]
                ?: return@runBlocking emptyList()

            runCatching {
                json.decodeFromString<List<StoredCookie>>(rawCookies)
            }.getOrDefault(emptyList())
        }
    }

    fun saveCookies(cookies: List<StoredCookie>) {
        runBlocking {
            context.cookieDataStore.edit { preferences ->
                preferences[cookiesKey] = json.encodeToString(cookies)
            }
        }
    }

    fun clearCookies() {
        runBlocking {
            context.cookieDataStore.edit { preferences ->
                preferences.remove(cookiesKey)
            }
        }
    }
}