package com.alexgreece45.marvelx.core.network.authenticators

import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.extensions.differenceInMilliseconds
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.core.network.controller.MarvelXBackendRequestTransformer
import com.alexgreece45.marvelx.core.network.di.model.ApiAuthenticator
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.alexgreece45.marvelx.core.network.model.api.token.toModel
import com.alexgreece45.marvelx.core.network.storage.ConsecutiveFailedAuthenticationStorage
import com.alexgreece45.marvelx.core.network.storage.TokenStorage
import com.alexgreece45.marvelx.model.model.ConsecutiveFailedAuthentication
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 * The authenticator class for all the API calls of the MarvelX backend. It is triggered when a 401
 * response is received.
 */
@Single
@ApiAuthenticator
internal class ApiAuthenticatorImpl(
    private val appLogger: AppLogger,
    private val configLogic: ConfigLogic,
    private val configOauthLogic: ConfigOauthLogic,
    private val refreshTokenClient: RefreshTokenClient,
    private val tokenStorage: TokenStorage,
    private val consecutiveFailedAuthenticationStorage: ConsecutiveFailedAuthenticationStorage,
    private val marvelXBackendRequestTransformer: MarvelXBackendRequestTransformer,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val sessionManager: SessionManager,
) : Authenticator {

    private val loggerTag = "ApiAuthenticator"

    /**
     * This function is triggered when a 401 response is received. It attempts to refresh the access
     * token 3 times before logging out the user.
     *
     * OkHttp may call this concurrently from several threads, so the whole process is serialized;
     * otherwise every concurrent 401 would refresh with the same (possibly already rotated) refresh
     * token.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // If the path is not related to the MarvelX backend, then the authentication process is not
        // initiated at all.
        val path = response.request.url.toUri().path
        if (!path.startsWith(configLogic.backendEndpointPrefix)) {
            return null
        }

        // Should never occur, but just in case.
        if (path == configOauthLogic.tokenPath) {
            return null
        }

        return synchronized(this) { authenticateLocked(response = response, path = path) }
    }

    private fun authenticateLocked(response: Response, path: String): Request? {
        // The request that caused the 401 error.
        val request = response.request

        // If the tokens were refreshed by another request while this one was in flight, retry it
        // with the current access token instead of refreshing again.
        val failedAuthorization = request.header(ConstantsSecurity.HeaderName.Authorization.name)
        val currentAccessTokenAndType = tokenStorage.getAccessTokenAndType()
        if (failedAuthorization != null &&
            currentAccessTokenAndType.isNotBlank() &&
            failedAuthorization != currentAccessTokenAndType
        ) {
            return marvelXBackendRequestTransformer.applyMarvelXBackendTransformations(
                originalRequest = request,
                path = path
            )
        }

        var shouldLogout = false
        when (val failedAuthData =
            consecutiveFailedAuthenticationStorage.getConsecutiveFailedAuthentication()) {
            null -> {
                consecutiveFailedAuthenticationStorage.setConsecutiveFailedAuthentication(
                    ConsecutiveFailedAuthentication(
                        timestamp = DateUtil.getUTCInstant(),
                        retries = 1
                    )
                )
            }

            else -> {
                // Check if the timestamps difference is within a certain bound. `configLogic.backendTimeoutData.connectTimeout * 1.5 * 3` is the same as in `withTimeout`
                // further below, multiplied by the max authentication tries + 10 seconds. The final number of milliseconds is: 77_500 (77.5 seconds).
                val isInBounds = DateUtil.getUTCInstant()
                    .differenceInMilliseconds(failedAuthData.timestamp) <= (configLogic.backendTimeoutData.connectTimeout * 1000) * 1.5 * 3 * configLogic.backendMaxAuthenticationTries + 10_000L

                val currentFailAuthData = ConsecutiveFailedAuthentication(
                    timestamp = DateUtil.getUTCInstant(),
                    retries = failedAuthData.retries + 1
                )
                consecutiveFailedAuthenticationStorage.setConsecutiveFailedAuthentication(
                    currentFailAuthData
                )

                if (currentFailAuthData.retries > configLogic.backendMaxAuthenticationTries) {
                    // Maximum retries have been surpassed and the request was performed too soon.
                    if (isInBounds) {
                        // Proceed with logout.
                        shouldLogout = true
                    } else {
                        // Reset timestamp and retries and proceed with token refresh.
                        consecutiveFailedAuthenticationStorage.setConsecutiveFailedAuthentication(
                            ConsecutiveFailedAuthentication(
                                timestamp = DateUtil.getUTCInstant(),
                                retries = 1
                            )
                        )
                    }
                }
            }
        }

        if (shouldLogout) {
//            authStore.removeUserCredentials()
            performLogoutOperations()
            return null
        }

        val clientId = configOauthLogic.clientId
        val refreshToken = tokenStorage.getRefreshToken()

        if (refreshToken.isNullOrBlank()) {
            performLogoutOperations()
            return null
        }

        var responseToken: ResponseToken? = null
        var timeoutExceeded = false
        runBlocking(ioDispatcher) {
            try {
                // 22.5 seconds
                val maxTimeoutSeconds =
                    (configLogic.backendTimeoutData.connectTimeout * 1.5).toLong().seconds

                withTimeout(maxTimeoutSeconds) {
                    repeat(configLogic.backendMaxAuthenticationTries) {
                        // If the token was retrieved from a previous call, exit the loop.
                        if (responseToken != null) {
                            return@repeat
                        }

                        // Obtain new tokens.
                        val response = refreshTokenClient.refreshAccessToken(clientId, refreshToken)
                        if (response.isSuccessful) {
                            when (val newResponseToken = response.body()) {
                                null -> {
                                    appLogger.e(loggerTag) { "(authenticate) error: Response body is null." }
                                }

                                else -> {
                                    tokenStorage.setAuthenticationTokens(newResponseToken.toModel())
                                    responseToken = newResponseToken
                                    appLogger.d(loggerTag) { "(authenticate) Success retrieving access token." }
                                }
                            }
                        } else {
                            appLogger.e(loggerTag) {
                                "(authenticate) error: ${response.errorBody()?.string().orEmpty()}"
                            }
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                timeoutExceeded = true
            }
        }

        // If the difference in seconds is more than 7.5 seconds then treat it as an error.
        if (timeoutExceeded) {
            appLogger.e(loggerTag) { "(authenticate) Timeout waiting for the token refresh." }

            performLogoutOperations()

            return null
        }

        if (responseToken == null) {
            performLogoutOperations()
            return null
        }

        // Save token info in device.
        tokenStorage.setAuthenticationTokens(responseToken.toModel())

        // Re-construct initial request with the new token.
        return marvelXBackendRequestTransformer.applyMarvelXBackendTransformations(
            originalRequest = request,
            path = path
        )
    }

    /**
     * The logout operations to perform.
     */
    private fun performLogoutOperations() {
        sessionManager.triggerLogout(LogoutReason.SESSION_EXPIRED)
    }
}