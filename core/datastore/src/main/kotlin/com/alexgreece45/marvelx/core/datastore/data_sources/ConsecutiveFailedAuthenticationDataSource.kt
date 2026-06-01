package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.extensions.removeData
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toEntity
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.ConsecutiveFailedAuthenticationEntity
import com.alexgreece45.marvelx.model.model.ConsecutiveFailedAuthentication
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Single

interface ConsecutiveFailedAuthenticationDataSource {
    suspend fun getConsecutiveFailedAuthentication(): ConsecutiveFailedAuthentication?
    suspend fun setConsecutiveFailedAuthentication(consecutiveFailedAuthentication: ConsecutiveFailedAuthentication)
    suspend fun removeConsecutiveFailedAuthentication()
}

@Single
internal class ConsecutiveFailedAuthenticationDataSourceImpl(
    private val dataStore: DataStore<ConsecutiveFailedAuthenticationEntity?>,
) : ConsecutiveFailedAuthenticationDataSource {

    override suspend fun getConsecutiveFailedAuthentication(): ConsecutiveFailedAuthentication? {
        return dataStore.data.firstOrNull()?.toModel()
    }

    override suspend fun setConsecutiveFailedAuthentication(consecutiveFailedAuthentication: ConsecutiveFailedAuthentication) {
        dataStore.updateDataCatchable { _ -> consecutiveFailedAuthentication.toEntity() }
    }

    override suspend fun removeConsecutiveFailedAuthentication() {
        dataStore.removeData()
    }
}