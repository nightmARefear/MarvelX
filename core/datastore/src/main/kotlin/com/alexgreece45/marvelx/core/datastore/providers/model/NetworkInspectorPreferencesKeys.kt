package com.alexgreece45.marvelx.core.datastore.providers.model

import androidx.datastore.preferences.core.stringPreferencesKey

internal object NetworkInspectorPreferencesKeys {
    const val DATA_STORE_FILE_NAME = "network_inspector_preferences"

    val SENSITIVE_NETWORK_DATA_MODE = stringPreferencesKey("sensitive_network_data_mode")
}
