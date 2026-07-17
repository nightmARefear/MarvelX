package com.alexgreece45.marvelx.core.common.config

/**
 * The configuration for the OAuth flow.
 */
interface ConfigOauthLogic {

    /**
     * The retrieve/refresh token path.
     */
    val tokenPath: String

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