package com.alexgreece45.marvelx.network_logic.authenticators

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.MarvelXBackendRequestController
import com.alexgreece45.marvelx.controllers.model.auth.CurrentConsecutiveFailedAuthenticationData
import com.alexgreece45.marvelx.data.repository.RefreshTokenRepository
import com.alexgreece45.marvelx.data.repository.TokenRefreshRepositoryResult
import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import com.alexgreece45.marvelx.network_logic.models_api.token.toUserAuthData
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.extensions.differenceInMilliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * The authenticator class for all the API calls of the MarvelX backend. Is is triggered when a
 * 401 response is received.
 */
class ApiAuthenticator(
    private val appLogger: AppLogger,
    private val configLogic: ConfigLogic,
    private val configOauth: ConfigOauth,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authController: AuthController,
    private val marvelXBackendRequestController: MarvelXBackendRequestController,
    private val routerHost: RouterHost
) : Authenticator {

    /**
     * The request that caused the 401 error.
     */
    private lateinit var request: Request

    /**
     * This function is triggered when a 401 response is received. It attempts to refresh the access
     * token 3 times before logging out the user.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // If the path is not related to the MarvelX backend, then the authentication process is not
        // initiated at all.
        val path = response.request.url.toUri().path
        if (!path.startsWith(BuildConfig.MARVELX_BACKEND_ENDPOINT_PREFIX)) {
            return null
        }

        // Should never occur, but just in case.
        if (path == configOauth.tokenPath) {
            return null
        }

        var shouldLogout = false
        when (val failedAuthData = authController.currentConsecutiveFailedAuthenticationData) {
            null -> {
                authController.currentConsecutiveFailedAuthenticationData =
                    CurrentConsecutiveFailedAuthenticationData(
                        timestamp = DateUtil.getUTCInstant(),
                        retries = 1
                    )
            }

            else -> {
                // Check if the timestamps difference is within a certain bound. `configLogic.backendTimeoutData.connectTimeout * 1.5 * 3` is the same as in `withTimeout`
                // further below, multiplied by the max authentication tries + 10 seconds. The final number of milliseconds is: 77_500 (77.5 seconds).
                val isInBounds = DateUtil.getUTCInstant()
                    .differenceInMilliseconds(failedAuthData.timestamp) <= (configLogic.backendTimeoutData.connectTimeout * 1000) * 1.5 * 3 * configLogic.backendMaxAuthenticationTries + 10_000L

                val currentFailAuthData = CurrentConsecutiveFailedAuthenticationData(
                    timestamp = DateUtil.getUTCInstant(),
                    retries = failedAuthData.retries + 1
                )
                authController.currentConsecutiveFailedAuthenticationData = currentFailAuthData

                if (currentFailAuthData.retries > configLogic.backendMaxAuthenticationTries) {
                    // Maximum retries have been surpassed and the request was performed too soon.
                    if (isInBounds) {
                        // Proceed with logout.
                        shouldLogout = true
                    } else {
                        // Reset timestamp and retries and proceed with token refresh.
                        authController.currentConsecutiveFailedAuthenticationData =
                            CurrentConsecutiveFailedAuthenticationData(
                                timestamp = DateUtil.getUTCInstant(),
                                retries = 1
                            )
                    }
                }
            }
        }

        if (shouldLogout) {
            authController.removeUserCredentials()
            performLogoutOperations()
            return null
        }

        // Set request.
        request = response.request

        val clientId = configOauth.clientId
        val refreshToken = authController.getRefreshToken()

        if (refreshToken.isNullOrBlank()) {
            authController.removeUserCredentials()
            performLogoutOperations()
            return null
        }

        var responseRefreshAccessToken: ResponseToken? = null
        var timeoutExceeded = false
        runBlocking(Dispatchers.IO) {
            try {
                // 22.5 seconds --> 22_500 milliseconds
                withTimeout((configLogic.backendTimeoutData.connectTimeout * 1.5.toLong()) * 1000) {
                    repeat(configLogic.backendMaxAuthenticationTries) {
                        // If the token was retrieved from a previous call, exit the loop.
                        if (responseRefreshAccessToken != null) {
                            return@repeat
                        }

                        // Same logic as in LoginInteractor.
                        refreshTokenRepository.refreshAccessToken(clientId, refreshToken).collect {
                            when (it) {
                                is TokenRefreshRepositoryResult.Failed -> {
                                    appLogger.e("ApiAuthenticator") { "(authenticate) error: ${it.failureMessage}}" }
                                }

                                is TokenRefreshRepositoryResult.Success -> {
                                    when (val responseToken = it.responseToken) {
                                        null -> {
                                            appLogger.e("ApiAuthenticator") { "(authenticate) error: Response body is null." }
                                        }

                                        else -> {
                                            authController.setData(responseToken.toUserAuthData())
                                            responseRefreshAccessToken = responseToken
                                            appLogger.d("ApiAuthenticator") { "(authenticate) Success retrieving access token." }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                timeoutExceeded = true
            }
        }

        // If the difference in seconds is more than 7.5 seconds then treat it as an error.
        if (timeoutExceeded) {
            appLogger.e("ApiAuthenticator") { "(authenticate) Timeout waiting for the token refresh." }

            authController.removeUserCredentials()
            performLogoutOperations()

            return null
        }

        if (responseRefreshAccessToken == null) {
            performLogoutOperations()
            return null
        }

        // Save token info in device.
        authController.setData(responseRefreshAccessToken!!.toUserAuthData())

        // Re-construct initial request with the new token.
        return marvelXBackendRequestController.applyMarvelXBackendTransformations(
            originalRequest = request,
            path = request.url.toUri().path
        )
    }

    /**
     * The logout operations to perform.
     */
    private fun performLogoutOperations() {
        NavArgHelper.setArgument(NavArg.HomeLogout, "reAuthenticationError")
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            routerHost.logout()
        }
    }
}
