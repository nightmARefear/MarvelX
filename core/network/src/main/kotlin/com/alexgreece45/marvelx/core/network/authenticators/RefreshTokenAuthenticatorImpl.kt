package com.alexgreece45.marvelx.core.network.authenticators

import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.network.di.model.RefreshTokenAuthenticator
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.annotation.Single

/**
 * The authenticator class for the `RefreshTokenClient`.
 */
@Single
@RefreshTokenAuthenticator
internal class RefreshTokenAuthenticatorImpl(
    private val appLogger: AppLogger,
    private val configOauthLogic: ConfigOauthLogic,
    private val configLogic: ConfigLogic,
    private val sessionManager: SessionManager,
) : Authenticator {

    private val loggerTag = "RefreshTokenAuthenticator"

    /**
     * This function is triggered when a 401 response is received. If the request's path is the
     * token one, then the user is instantly logged out.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // If the path is not related to the MarvelX backend, then proceed.
        val path = response.request.url.toUri().path
        if (!path.startsWith(configLogic.backendEndpointPrefix)) {
            return null
        }

        // If the path is the token path then logout.
        if (path == configOauthLogic.tokenPath) {
            appLogger.e(loggerTag) { "(authenticate) An authorization error occurred whilst trying to refresh the access token." }
            sessionManager.triggerLogout(LogoutReason.SESSION_EXPIRED)
        }
        return null
    }
}