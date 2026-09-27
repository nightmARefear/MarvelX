package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.alexgreece45.marvelx.core.datastore.providers.model.NetworkInspectorPreferencesKeys
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Each case writes at most once per file: on a Windows host, DataStore's JVM file storage can't
 * replace an existing file, so repeated writes to the same file fail there (not on Android).
 */
class NetworkInspectorDataSourceTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `GIVEN nothing stored WHEN getSensitiveNetworkDataMode THEN returns UNMASKED`() = runBlocking {
        val dataSource = NetworkInspectorDataSourceImpl(newDataStore("empty"))

        assertEquals(SensitiveNetworkDataMode.UNMASKED, dataSource.getSensitiveNetworkDataMode())
    }

    @Test
    fun `GIVEN a stored mode WHEN getSensitiveNetworkDataMode THEN returns it`() = runBlocking {
        SensitiveNetworkDataMode.entries.forEach { mode ->
            val dataSource = NetworkInspectorDataSourceImpl(newDataStore(mode.name))

            dataSource.setSensitiveNetworkDataMode(mode)

            assertEquals(mode, dataSource.getSensitiveNetworkDataMode())
        }
    }

    @Test
    fun `GIVEN an unknown stored value WHEN getSensitiveNetworkDataMode THEN falls back to UNMASKED`() = runBlocking {
        val dataStore = newDataStore("unknown")
        dataStore.edit { it[NetworkInspectorPreferencesKeys.SENSITIVE_NETWORK_DATA_MODE] = "SOMETHING_ELSE" }

        assertEquals(
            SensitiveNetworkDataMode.UNMASKED,
            NetworkInspectorDataSourceImpl(dataStore).getSensitiveNetworkDataMode()
        )
    }

    private fun newDataStore(name: String): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { temporaryFolder.root.resolve("$name.preferences_pb") }
        )
}
