package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.alexgreece45.marvelx.core.datastore.extensions.editValue
import com.alexgreece45.marvelx.core.datastore.extensions.getValue
import com.alexgreece45.marvelx.core.datastore.providers.model.NetworkInspectorPreferences
import com.alexgreece45.marvelx.core.datastore.providers.model.NetworkInspectorPreferencesKeys
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import org.koin.core.annotation.Single

/**
 * Developer preferences of the network inspector (Chucker). Not sensitive by itself, so it's kept
 * in plain (unencrypted) preferences.
 */
interface NetworkInspectorDataSource {
    suspend fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode
    suspend fun setSensitiveNetworkDataMode(mode: SensitiveNetworkDataMode)
}

@Single
internal class NetworkInspectorDataSourceImpl(
    @NetworkInspectorPreferences private val dataStore: DataStore<Preferences>
) : NetworkInspectorDataSource {

    private val key = NetworkInspectorPreferencesKeys.SENSITIVE_NETWORK_DATA_MODE

    override suspend fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode {
        val storedMode = dataStore.getValue(key = key, defaultValue = null)
        return SensitiveNetworkDataMode.entries.firstOrNull { it.name == storedMode }
            ?: SensitiveNetworkDataMode.default
    }

    override suspend fun setSensitiveNetworkDataMode(mode: SensitiveNetworkDataMode) {
        dataStore.editValue(key = key, value = mode.name)
    }
}
