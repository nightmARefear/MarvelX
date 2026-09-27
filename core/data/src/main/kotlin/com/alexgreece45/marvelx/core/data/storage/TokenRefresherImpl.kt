package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.core.network.model.api.token.toModel
import com.alexgreece45.marvelx.core.network.storage.TokenRefreshResult
import com.alexgreece45.marvelx.core.network.storage.TokenRefresher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class TokenRefresherImpl(
    private val refreshTokenClient: RefreshTokenClient,
    private val userSessionRepository: UserSessionRepository,
    private val configOauthLogic: ConfigOauthLogic,
    private val appLogger: AppLogger,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : TokenRefresher {

    private val loggerTag = "TokenRefresher"

    private val mutex = Mutex()
    private var inFlightRefresh: Deferred<TokenRefreshResult>? = null

    override suspend fun refresh(): TokenRefreshResult {
        val refresh = mutex.withLock {
            inFlightRefresh?.takeIf { it.isActive }
                // Runs in the application scope, so a caller giving up (e.g., the authenticator's
                // timeout) doesn't cancel a refresh whose response still has to be persisted.
                ?: applicationScope.async(ioDispatcher) { performRefresh() }
                    .also { inFlightRefresh = it }
        }
        return refresh.await()
    }

    private suspend fun performRefresh(): TokenRefreshResult {
        return try {
            val refreshToken = userSessionRepository.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return TokenRefreshResult.Failed(reason = "No refresh token is stored.")
            }

            val response = refreshTokenClient.refreshAccessToken(configOauthLogic.clientId, refreshToken)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                appLogger.e(loggerTag) { "(refresh) Refresh failed with HTTP ${response.code()}." }
                return TokenRefreshResult.Failed(reason = "Refresh failed with HTTP ${response.code()}.")
            }

            val authenticationTokens = body.toModel()
            userSessionRepository.setAuthenticationTokens(authenticationTokens)
            appLogger.d(loggerTag) { "(refresh) Access token refreshed." }
            TokenRefreshResult.Success(authenticationTokens)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "(refresh) error: ${e.message.orEmpty()}" }
            TokenRefreshResult.Error(e)
        }
    }
}
