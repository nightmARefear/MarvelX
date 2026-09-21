package com.alexgreece45.marvelx.controllers

import androidx.core.net.toUri
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.annotation.Single

@Single(binds = [OAuthAuthorizationController::class])
internal class OAuthAuthorizationControllerImpl(
    private val configLogic: ConfigLogic,
    private val configOauthLogic: ConfigOauthLogic,
    appLogger: AppLogger,
) : AbstractOAuthAuthorizationController(appLogger) {

    override fun buildAuthorizationUrl(tempAuthCode: String): String {
        val (verifier, state) = startNewAttempt()

        return (configLogic.serverUrl + configOauthLogic.authorizationPath).toUri()
            .buildUpon()
            .appendQueryParameter("temp_auth_code", tempAuthCode)
            .appendQueryParameter("client_id", configOauthLogic.clientId)
            .appendQueryParameter("code_challenge", createCodeChallenge(verifier))
            .appendQueryParameter("code_challenge_method", configOauthLogic.codeChallengeMethod)
            .appendQueryParameter("redirect_uri", configOauthLogic.redirectUri)
            .appendQueryParameter("state", state)
            .build()
            .toString()
    }
}
