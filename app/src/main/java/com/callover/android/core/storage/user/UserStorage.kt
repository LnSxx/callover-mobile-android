package com.callover.android.core.storage.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.callover.android.core.domain.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore by preferencesDataStore(
    name = "callover_user_store",
)

@Singleton
class UserStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    fun loadUser(): User? {
        return runBlocking {
            val rawUser = context.userDataStore.data.first()[USER_KEY]
                ?: return@runBlocking null

            runCatching {
                json.decodeFromString<StoredUser>(rawUser).toDomain()
            }.getOrNull()
        }
    }

    fun saveUser(user: User) {
        runBlocking {
            context.userDataStore.edit { preferences ->
                preferences[USER_KEY] = json.encodeToString(user.toStoredUser())
            }
        }
    }

    fun clearUser() {
        runBlocking {
            context.userDataStore.edit { preferences ->
                preferences.remove(USER_KEY)
            }
        }
    }

    private companion object {
        val USER_KEY = stringPreferencesKey("user")
    }
}