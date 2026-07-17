package com.alexgreece45.marvelx.core.datastore.providers.model

import androidx.datastore.preferences.core.stringPreferencesKey

internal object FcmTokenPreferencesKeys {
    const val DATA_STORE_FILE_NAME = "fcm_token_preferences"

    val FCM_TOKEN = stringPreferencesKey("fcm_token")
}