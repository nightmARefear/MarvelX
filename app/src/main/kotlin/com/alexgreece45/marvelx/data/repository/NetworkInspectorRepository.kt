package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.datastore.data_sources.NetworkInspectorDataSource
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import org.koin.core.annotation.Single

/**
 * Developer preferences of the network inspector (Chucker), available in debug/mock builds.
 */
interface NetworkInspectorRepository {
    suspend fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode
    suspend fun setSensitiveNetworkDataMode(mode: SensitiveNetworkDataMode)
}

@Single
internal class NetworkInspectorRepositoryImpl(
    private val networkInspectorDataSource: NetworkInspectorDataSource
) : NetworkInspectorRepository {

    override suspend fun getSensitiveNetworkDataMode(): SensitiveNetworkDataMode =
        networkInspectorDataSource.getSensitiveNetworkDataMode()

    override suspend fun setSensitiveNetworkDataMode(mode: SensitiveNetworkDataMode) {
        networkInspectorDataSource.setSensitiveNetworkDataMode(mode)
    }
}
