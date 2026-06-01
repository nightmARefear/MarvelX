package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.alexgreece45.marvelx.core.datastore.extensions.editValue
import com.alexgreece45.marvelx.core.datastore.extensions.getValue
import com.alexgreece45.marvelx.core.datastore.extensions.removeKey
import com.alexgreece45.marvelx.core.datastore.providers.model.FcmTokenPreferences
import com.alexgreece45.marvelx.core.datastore.providers.model.FcmTokenPreferencesKeys
import org.koin.core.annotation.Single

interface FcmTokenDataSource {

    suspend fun getFcmToken(): String?
    suspend fun setFcmToken(fcmToken: String)
    suspend fun removeFcmToken()
}

@Single
internal class FcmTokenDataSourceImpl(
    @FcmTokenPreferences private val dataStore: DataStore<Preferences>
) : FcmTokenDataSource {

    private val key = FcmTokenPreferencesKeys.FCM_TOKEN

    override suspend fun getFcmToken(): String? = dataStore.getValue(
        key = key,
        defaultValue = null
    )

    override suspend fun setFcmToken(fcmToken: String) {
        dataStore.editValue(
            key = key,
            value = fcmToken
        )
    }

    override suspend fun removeFcmToken() {
        dataStore.removeKey(key)
    }
}