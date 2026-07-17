package com.alexgreece45.marvelx.core.datastore.providers.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

internal object AutoLogoutPreferencesKeys {
    const val DATA_STORE_FILE_NAME = "auto_logout_preferences"

    val SHOULD_REMEMBER_USER_SESSION = booleanPreferencesKey("should_remember_user_session")
    val AUTO_LOGOUT_TIMESTAMP = longPreferencesKey("auto_logout_timestamp")
}