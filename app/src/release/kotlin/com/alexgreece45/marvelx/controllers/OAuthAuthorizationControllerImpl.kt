package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.PendingAuthorizationRepository
import org.koin.core.annotation.Single

@Single(binds = [OAuthAuthorizationController::class])
internal class OAuthAuthorizationControllerImpl(
    private val configLogic: ConfigLogic,
    private val configOauthLogic: ConfigOauthLogic,
    pendingAuthorizationRepository: PendingAuthorizationRepository,
    appLogger: AppLogger,
) : AbstractOAuthAuthorizationController(appLogger, pendingAuthorizationRepository, configOauthLogic) {

    override suspend fun startAuthorization(
        tempAuthCode: String,
        loginContext: OAuthLoginContext
    ): OAuthAuthorizationRequest {
        val attempt = startNewAttempt(loginContext)

        val url = Uri.parse(configLogic.serverUrl + configOauthLogic.authorizationPath)
            .buildUpon()
            .appendQueryParameter("temp_auth_code", tempAuthCode)
            .appendQueryParameter("client_id", configOauthLogic.clientId)
            .appendQueryParameter("code_challenge", attempt.codeChallenge)
            .appendQueryParameter("code_challenge_method", configOauthLogic.codeChallengeMethod)
            .appendQueryParameter("redirect_uri", attempt.redirectUri)
            .appendQueryParameter("state", attempt.state)
            .build()
            .toString()

        return OAuthAuthorizationRequest(url = url, redirect = redirect, state = attempt.state)
    }
}
