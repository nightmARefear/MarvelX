package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.storage.TokenStorage
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class TokenStorageImpl(
    private val userSessionRepository: UserSessionRepository,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : TokenStorage {

    override fun getAccessTokenAndType(): String {
        return runBlocking(ioDispatcher) {
            userSessionRepository.getAccessTokenAndType()
        }
    }

    override fun getRefreshToken(): String? {
        return runBlocking(ioDispatcher) {
            userSessionRepository.getRefreshToken()
        }
    }

    override fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens) {
        return runBlocking(ioDispatcher) {
            userSessionRepository.setAuthenticationTokens(authenticationTokens)
        }
    }
}