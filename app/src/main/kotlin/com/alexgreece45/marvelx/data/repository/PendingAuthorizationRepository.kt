package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.datastore.data_sources.PendingAuthorizationDataSource
import com.alexgreece45.marvelx.model.model.PendingAuthorization
import org.koin.core.annotation.Single

/**
 * Persists the OAuth 2.0 + PKCE authorization request that is currently in the browser, so the
 * login can be completed even if the app process is killed before the redirect arrives.
 */
interface PendingAuthorizationRepository {
    suspend fun getPendingAuthorization(): PendingAuthorization?
    suspend fun setPendingAuthorization(pendingAuthorization: PendingAuthorization)
    suspend fun removePendingAuthorization()
}

@Single
internal class PendingAuthorizationRepositoryImpl(
    private val pendingAuthorizationDataSource: PendingAuthorizationDataSource
) : PendingAuthorizationRepository {

    override suspend fun getPendingAuthorization(): PendingAuthorization? =
        pendingAuthorizationDataSource.getPendingAuthorization()

    override suspend fun setPendingAuthorization(pendingAuthorization: PendingAuthorization) {
        pendingAuthorizationDataSource.setPendingAuthorization(pendingAuthorization)
    }

    override suspend fun removePendingAuthorization() {
        pendingAuthorizationDataSource.removePendingAuthorization()
    }
}
