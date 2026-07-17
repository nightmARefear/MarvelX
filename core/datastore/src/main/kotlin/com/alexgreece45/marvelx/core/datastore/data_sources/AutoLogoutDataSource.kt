package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.alexgreece45.marvelx.core.datastore.extensions.editValue
import com.alexgreece45.marvelx.core.datastore.extensions.getValue
import com.alexgreece45.marvelx.core.datastore.extensions.removeKey
import com.alexgreece45.marvelx.core.datastore.extensions.removeKeys
import com.alexgreece45.marvelx.core.datastore.providers.model.AutoLogoutPreferences
import com.alexgreece45.marvelx.core.datastore.providers.model.AutoLogoutPreferencesKeys
import org.koin.core.annotation.Single
import java.time.Instant

interface AutoLogoutDataSource {
    suspend fun getRememberUserSession(): Boolean
    suspend fun setRememberUserSession(remember: Boolean)
    suspend fun removeRememberUserSession()

    suspend fun getTimestamp(): Instant?
    suspend fun setTimestamp(timestamp: Instant)
    suspend fun removeTimestamp()

    suspend fun removeAll()
}

@Single
internal class AutoLogoutDataSourceImpl(
    @AutoLogoutPreferences private val dataStore: DataStore<Preferences>
) : AutoLogoutDataSource {

    override suspend fun getRememberUserSession(): Boolean =
        dataStore.getValue(
            key = AutoLogoutPreferencesKeys.SHOULD_REMEMBER_USER_SESSION,
            defaultValue = false
        ) ?: false

    override suspend fun setRememberUserSession(remember: Boolean) {
        dataStore.editValue(
            key = AutoLogoutPreferencesKeys.SHOULD_REMEMBER_USER_SESSION,
            value = remember
        )
    }

    override suspend fun removeRememberUserSession() {
        dataStore.removeKey(
            key = AutoLogoutPreferencesKeys.SHOULD_REMEMBER_USER_SESSION
        )
    }

    override suspend fun getTimestamp(): Instant? {
        val milliseconds =
            dataStore.getValue(
                key = AutoLogoutPreferencesKeys.AUTO_LOGOUT_TIMESTAMP,
                defaultValue = null
            ) ?: return null
        return try {
            Instant.ofEpochMilli(milliseconds)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun setTimestamp(timestamp: Instant) {
        dataStore.editValue(
            key = AutoLogoutPreferencesKeys.AUTO_LOGOUT_TIMESTAMP,
            value = timestamp.toEpochMilli()
        )
    }

    override suspend fun removeTimestamp() {
        dataStore.removeKey(key = AutoLogoutPreferencesKeys.AUTO_LOGOUT_TIMESTAMP)
    }

    override suspend fun removeAll() {
        dataStore.removeKeys(
            AutoLogoutPreferencesKeys.SHOULD_REMEMBER_USER_SESSION,
            AutoLogoutPreferencesKeys.AUTO_LOGOUT_TIMESTAMP
        )
    }
}