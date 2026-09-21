package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.annotation.Single

/**
 * The `mock` flavor has no real backend to redirect through, so instead of opening a Custom Tab
 * against a real `/authorize` endpoint, this fabricates the redirect URI directly (with a fake
 * `code` and the real generated `state`). Launching it still round-trips through the OS's verified
 * App Link resolution straight back into the app — no network call, no divergent code path in the
 * screen/ViewModel layer above this controller.
 */
@Single(binds = [OAuthAuthorizationController::class])
internal class OAuthAuthorizationControllerImpl(
    private val configOauthLogic: ConfigOauthLogic,
    appLogger: AppLogger,
) : AbstractOAuthAuthorizationController(appLogger) {

    override fun buildAuthorizationUrl(tempAuthCode: String): String {
        val (_, state) = startNewAttempt()

        return Uri.parse(configOauthLogic.redirectUri)
            .buildUpon()
            .appendQueryParameter("code", MOCK_AUTH_CODE)
            .appendQueryParameter("state", state)
            .build()
            .toString()
    }

    private companion object {
        const val MOCK_AUTH_CODE = "AUTH_CODE"
    }
}
