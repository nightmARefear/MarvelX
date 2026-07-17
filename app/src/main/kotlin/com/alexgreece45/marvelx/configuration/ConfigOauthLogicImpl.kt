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
        get() = "path"

    override val redirectUri: String
        get() = "marvelx_backend//oauth"
}