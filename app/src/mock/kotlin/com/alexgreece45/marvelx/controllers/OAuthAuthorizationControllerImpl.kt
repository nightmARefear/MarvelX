package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.PendingAuthorizationRepository
import org.koin.core.annotation.Single

/**
 * The `mock` flavor has no real backend to redirect through, so instead of opening a browser
 * against a real `/authorize` endpoint, this fabricates the redirect URI directly (with a fake
 * `code` and the real generated `state`). The launcher hands a non-`https` URL straight to the app
 * as an explicit `VIEW` intent, so it still round-trips through `MainActivity`'s deep link handling
 * — no network call, no divergent code path in the screen/ViewModel layer above this controller.
 */
@Single(binds = [OAuthAuthorizationController::class])
internal class OAuthAuthorizationControllerImpl(
    configOauthLogic: ConfigOauthLogic,
    pendingAuthorizationRepository: PendingAuthorizationRepository,
    appLogger: AppLogger,
) : AbstractOAuthAuthorizationController(appLogger, pendingAuthorizationRepository, configOauthLogic) {

    override suspend fun startAuthorization(
        tempAuthCode: String,
        loginContext: OAuthLoginContext
    ): OAuthAuthorizationRequest {
        val attempt = startNewAttempt(loginContext)

        val url = Uri.parse(attempt.redirectUri)
            .buildUpon()
            .appendQueryParameter("code", MOCK_AUTH_CODE)
            .appendQueryParameter("state", attempt.state)
            .build()
            .toString()

        return OAuthAuthorizationRequest(url = url, redirect = redirect, state = attempt.state)
    }

    private companion object {
        const val MOCK_AUTH_CODE = "AUTH_CODE"
    }
}
