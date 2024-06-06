package com.alexgreece45.marvelx.configuration

/**
 * The configuration for the OAuth flow.
 */
interface ConfigOauth {
    /**
     * The client ID -- i.e., if the request is sent from the mobile/web app.
     */
    val clientId: String

    /**
     * The hashing algorithm (method) of the code challenge.
     */
    val codeChallengeMethod: String

    /**
     * The authorization path.
     */
    val authorizationPath: String

    /**
     * The redirect URI.
     */
    val redirectUri: String
}

class ConfigOauthImpl : ConfigOauth {
    override val clientId: String
        get() = "mobile"
    override val codeChallengeMethod: String
        get() = "S256"
    override val authorizationPath: String
        get() = "path"
    override val redirectUri: String
        get() = "marvelx_backend//oauth"
}