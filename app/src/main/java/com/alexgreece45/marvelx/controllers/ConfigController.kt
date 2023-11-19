package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.TOKEN_CLIENT_ID
import com.alexgreece45.marvelx.util.TOKEN_REDIRECT_URI
import javax.inject.Inject

interface ConfigController {
    /**
     * Returns the client ID.
     */
    val clientId: String

    /**
     * Returns the client secret.
     */
    val clientSecret: String

    /**
     * The refresh interval, in minutes, for refreshing the `access` token.
     *
     * #### Note: It is ALWAYS one minute less than the expiration timespan of the access token, so that we can request for a new token within 10 to 20 seconds before it expires.
     *
     */
    val refreshTokenInterval: Long

    /**
     * Returns the URI.
     */
    val uri: String

    /**
     * Extracts and returns the scheme (i.e., `https`, `http`) from the base URL.
     */
    val scheme: String

    /**
     * Returns the host.
     */
    val host: String

    /**
     * Extracts and returns the port (i.e., `XXXXX`, where `X` integer) from the base URL.
     */
    val port: Int
}

class ConfigControllerImpl @Inject constructor() : ConfigController {
    /**
     * The actual refresh token interval.
     */
    private val actualRefreshTokenInterval: Long = 10L

    override val clientId: String get() = TOKEN_CLIENT_ID
    override val clientSecret: String get() = BuildConfig.TOKEN_CLIENT_SECRET
    override val refreshTokenInterval: Long get() = actualRefreshTokenInterval - 1L
    override val uri: String get() = TOKEN_REDIRECT_URI
    override val scheme: String
        get() =
            if (BuildConfig.MARVELX_BACKEND_BASE_URL.startsWith("https")) "https" else "http"
    override val host: String get() = BuildConfig.MARVELX_BACKEND_DOMAIN
    override val port: Int
        get() =
            BuildConfig.MARVELX_BACKEND_BASE_URL.split(":").last().toInt()
}