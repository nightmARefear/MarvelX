package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.UserSessionDataSource
import com.alexgreece45.marvelx.core.network.storage.TokenStorage
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class TokenStorageImpl(
    private val userSessionDataSource: UserSessionDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : TokenStorage {

    override fun getAccessToken(): String {
        return runBlocking(ioDispatcher) {
            userSessionDataSource.getUserSession()?.authenticationTokens?.accessToken.orEmpty()
        }
    }

    override fun getRefreshToken(): String? {
        return runBlocking(ioDispatcher) {
            userSessionDataSource.getUserSession()?.authenticationTokens?.refreshToken
        }
    }

    override fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens) {
        return runBlocking(ioDispatcher) {
            userSessionDataSource.setUserSession { currentUserSession ->
                currentUserSession.copy(
                    authenticationTokens = authenticationTokens
                )
            }
        }
    }
}