package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.extensions.getData
import com.alexgreece45.marvelx.core.datastore.extensions.removeDataCatchable
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toEntity
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.PendingAuthorizationEntity
import com.alexgreece45.marvelx.core.datastore.providers.model.PendingAuthorizationDataStore
import com.alexgreece45.marvelx.model.model.PendingAuthorization
import org.koin.core.annotation.Single

/**
 * Stores the OAuth 2.0 + PKCE authorization request that is currently in the browser, so that it
 * survives process death. It holds a `code_verifier`, so it is encrypted at rest like the tokens.
 */
interface PendingAuthorizationDataSource {
    suspend fun getPendingAuthorization(): PendingAuthorization?
    suspend fun setPendingAuthorization(pendingAuthorization: PendingAuthorization)
    suspend fun removePendingAuthorization()
}

@Single
internal class PendingAuthorizationDataSourceImpl(
    @PendingAuthorizationDataStore private val dataStore: DataStore<PendingAuthorizationEntity?>,
) : PendingAuthorizationDataSource {

    override suspend fun getPendingAuthorization(): PendingAuthorization? {
        return dataStore.getData()?.toModel()
    }

    override suspend fun setPendingAuthorization(pendingAuthorization: PendingAuthorization) {
        dataStore.updateDataCatchable { _ -> pendingAuthorization.toEntity() }
    }

    override suspend fun removePendingAuthorization() {
        dataStore.removeDataCatchable()
    }
}
