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
     * The authorization path, opened in a browser (Custom Tabs) as part of the PKCE authorization
     * request. Relative to the backend's base URL.
     */
    val authorizationPath: String

    /**
     * The redirect URI the backend redirects back to once the browser-based authorization request
     * completes. Must exactly match the app's verified deep link (see `AndroidManifest.xml`'s
     * `android.intent.action.VIEW` intent filter) and the backend's allowlisted value.
     */
    val redirectUri: String
}