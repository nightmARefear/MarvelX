package com.alexgreece45.marvelx.network_logic.authenticators

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * The authenticator class for the `RefreshTokenClient`.
 */
class RefreshTokenAuthenticator(
    private val appLogger: AppLogger,
    private val configOauth: ConfigOauth,
    private val routerHost: RouterHost
) : Authenticator {

    /**
     * This function is triggered when a 401 response is received. If the request's path is the
     * token one, then the user is instantly logged out.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // If the path is not related to the MarvelX backend, then proceed.
        val path = response.request.url.toUri().path
        if (!path.startsWith(BuildConfig.MARVELX_BACKEND_ENDPOINT_PREFIX)) {
            return null
        }

        // If the path is the token path then logout.
        if (path == configOauth.tokenPath) {
            appLogger.e("RefreshTokenAuthenticator") { "(authenticate) An authorization occurred whilst trying to refresh the access token." }
            performLogoutOperations()
        }
        return null
    }

    /**
     * The logout operations to perform.
     */
    private fun performLogoutOperations() {
        NavArgHelper.setArgument(NavArg.HomeLogout, "reAuthenticationNoAuthorizationError")
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            routerHost.logout()
        }
    }
}
