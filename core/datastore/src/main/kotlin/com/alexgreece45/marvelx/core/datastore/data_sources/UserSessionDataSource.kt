package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.extensions.getData
import com.alexgreece45.marvelx.core.datastore.extensions.removeDataCatchable
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toEntity
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.UserSessionEntity
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.alexgreece45.marvelx.model.model.UserSession
import org.koin.core.annotation.Single

interface UserSessionDataSource {

    suspend fun getUserSession(): UserSession?
    suspend fun setUserSession(transform: (currentUserSession: UserSession) -> UserSession)
    suspend fun removeUserSession()
}

@Single
internal class UserSessionDataSourceImpl(
    private val userSessionDataStore: DataStore<UserSessionEntity?>,
) : UserSessionDataSource {

    override suspend fun getUserSession(): UserSession? {
        return userSessionDataStore.getData()?.toModel()
    }

    override suspend fun setUserSession(transform: (currentUserSession: UserSession) -> UserSession) {
        userSessionDataStore.updateDataCatchable { userSession ->
            userSession?.toModel()?.let { currentUserSession ->
                transform(currentUserSession).toEntity()
            }
        }
    }

    override suspend fun removeUserSession() {
        userSessionDataStore.removeDataCatchable()
    }
}