package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import org.koin.core.annotation.Single

@Single(binds = [ConfigOauthLogic::class])
internal class ConfigOauthLogicImpl : ConfigOauthLogic {

    override val tokenPath: String
        get() = BuildConfig.MARVELX_BACKEND_PATH_TOKEN

    override val clientId: String
        get() = "mobile"

    override val codeChallengeMethod: String
        get() = "S256"

    override val authorizationPath: String
        get() = "/api/private/marvelx/user/authorize"

    override val isHttpsRedirectEnabled: Boolean
        get() = BuildConfig.OAUTH_HTTPS_REDIRECT_ENABLED

    override val redirectScheme: String
        get() = if (isHttpsRedirectEnabled) "https" else BuildConfig.DEEPLINK_SCHEME

    override val redirectHost: String
        get() = BuildConfig.MARVELX_BACKEND_DOMAIN

    override val redirectPath: String
        get() = "/deeplink/oauth"

    override val redirectUri: String
        get() = "$redirectScheme://$redirectHost$redirectPath"
}
