package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.models.MarvelApiConfig
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.extensions.getPortFromUrl
import com.alexgreece45.marvelx.util.extensions.getSchemeFromUrl

interface ConfigLogic {
    /**
     * The client ID.
     */
    val clientId: String

    /**
     * The client secret.
     */
    val clientSecret: String

    /**
     * The refresh interval, in minutes, for refreshing the `access` token. Its value is set to `15` by default.
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

    /**
     * The MarvelX backend URL.
     */
    val serverUrl: String

    /**
     * The websocket URL.
     */
    val websocketUrl: String

    /**
     * The network config for the Marvel API.
     */
    val marvelApiConfig: MarvelApiConfig

    /**
     * Defines whether screen capturing is blocked. It has a default value of `false`.
     */
    val blockScreenCapture: Boolean get() = false

    /**
     * Defines whether overlay windows are hidden. It has a default value of `false`.
     */
    val hideOverlayWindows: Boolean get() = false

    /**
     * Defines whether this application should have the ability to draw overlay windows over other
     * applications.
     */
    val applicationDrawOverlayWindows: Boolean get() = false

    /**
     * Defines whether this application should have the ability to be in multi window mode.
     */
    val applicationMultiWindowMode: Boolean get() = false

    /**
     * Defines whether auto logout is enabled.
     */
    val isAutoLogoutEnabled: Boolean

    /**
     * The app's build type (i.e., debug, mock, or release).
     */
    val appBuildType: AppBuildType

    /**
     * The package signature of this application.
     */
    val packageSignature: String
}

sealed class AppBuildType(val key: String) {
    data object DEBUG : AppBuildType(key = "debug")
    data object MOCK : AppBuildType(key = "mock")
    data object RELEASE : AppBuildType(key = "release")

    companion object {
        fun getType(): AppBuildType =
            when (BuildConfig.BUILD_TYPE) {
                DEBUG.key -> DEBUG
                MOCK.key -> MOCK
                RELEASE.key -> RELEASE
                else -> RELEASE
            }
    }
}

class ConfigLogicImpl : ConfigLogic {

    override val clientId: String get() = ConstantsSecurity.OAuth.TOKEN_CLIENT_ID
    override val clientSecret: String get() = BuildConfig.TOKEN_CLIENT_SECRET
    override val refreshTokenInterval: Long get() = 15L
    override val uri: String get() = ConstantsSecurity.OAuth.TOKEN_REDIRECT_URI
    override val scheme: String
        get() = BuildConfig.MARVELX_BACKEND_BASE_URL.getSchemeFromUrl()
    override val host: String get() = BuildConfig.MARVELX_BACKEND_DOMAIN
    override val port: Int
        get() = BuildConfig.MARVELX_BACKEND_BASE_URL.getPortFromUrl()

    override val serverUrl: String
        get() = BuildConfig.MARVELX_BACKEND_BASE_URL

    override val websocketUrl: String
        get() = BuildConfig.MARVELX_BACKEND_WEBSOCKET_BASE_URL

    override val marvelApiConfig: MarvelApiConfig
        get() = MarvelApiConfig(
            scheme = BuildConfig.MARVEL_API_BASE_URL.getSchemeFromUrl(),
            host = BuildConfig.MARVEL_API_DOMAIN,
            endpointPrefix = BuildConfig.MARVEL_API_ENDPOINT_PREFIX
        )

    override val isAutoLogoutEnabled: Boolean
        get() = false

    override val appBuildType: AppBuildType
        get() = AppBuildType.getType()

    override val packageSignature: String
        get() = "tPwQygNBC+Pl1e3T5qwNGCg/oy9wOM9p+c4oddwuGFE="
}