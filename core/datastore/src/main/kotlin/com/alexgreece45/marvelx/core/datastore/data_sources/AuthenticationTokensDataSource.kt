package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.providers.model.AuthenticationTokensDataStore
import com.alexgreece45.marvelx.core.datastore.extensions.getData
import com.alexgreece45.marvelx.core.datastore.extensions.removeDataCatchable
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toEntity
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.AuthenticationTokensEntity
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import org.koin.core.annotation.Single

interface AuthenticationTokensDataSource {
    suspend fun getAuthenticationTokens(): AuthenticationTokens?
    suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens)
    suspend fun removeAuthenticationTokens()
}

@Single
internal class AuthenticationTokensDataSourceImpl(
    @AuthenticationTokensDataStore private val authenticationTokensDataStore: DataStore<AuthenticationTokensEntity?>
) : AuthenticationTokensDataSource {

    override suspend fun getAuthenticationTokens(): AuthenticationTokens? =
        authenticationTokensDataStore.getData()?.toModel()

    override suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens) {
        authenticationTokensDataStore.updateDataCatchable { _ ->
            authenticationTokens.toEntity()
        }
    }

    override suspend fun removeAuthenticationTokens() {
        authenticationTokensDataStore.removeDataCatchable()
    }
}