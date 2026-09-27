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
     * Whether the authorization redirect uses the verified `https` App Link instead of the legacy
     * custom scheme. It must only be enabled once the backend serves this build's signing
     * certificate in `/.well-known/assetlinks.json` and allowlists the `https` redirect URI.
     */
    val isHttpsRedirectEnabled: Boolean

    /**
     * The scheme of [redirectUri]: `https` when [isHttpsRedirectEnabled], otherwise the build
     * type's custom deep link scheme.
     */
    val redirectScheme: String

    /**
     * The host of [redirectUri].
     */
    val redirectHost: String

    /**
     * The path of [redirectUri].
     */
    val redirectPath: String

    /**
     * The redirect URI the backend redirects back to once the browser-based authorization request
     * completes. Must exactly match one of the app's `android.intent.action.VIEW` intent filters in
     * `AndroidManifest.xml` and the backend's allowlisted values.
     */
    val redirectUri: String
}