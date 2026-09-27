package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.NetworkInspectorDataSource
import com.alexgreece45.marvelx.core.network.storage.NetworkInspectorSettings
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class NetworkInspectorSettingsImpl(
    private val networkInspectorDataSource: NetworkInspectorDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : NetworkInspectorSettings {

    override fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode {
        return runBlocking(ioDispatcher) {
            networkInspectorDataSource.getSensitiveNetworkDataMode()
        }
    }
}
