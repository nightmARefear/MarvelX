package com.alexgreece45.marvelx.core.common.config

/**
 * The configuration for the token endpoint (token refresh) and logout.
 */
interface ConfigOauthLogic {

    /**
     * The refresh token path.
     */
    val tokenPath: String

    /**
     * The client ID -- i.e., if the request is sent from the mobile/web app.
     */
    val clientId: String
}
