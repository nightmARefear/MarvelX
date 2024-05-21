package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import okhttp3.WebSocketListener

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
     * The websocket URL.
     */
    val websocketUrl: String

    /**
     * Defines whether screen capturing is blocked. It has a default value of `false`.
     */
    val blockScreenCapture: Boolean get() = false

    /**
     * Defines whether overlay windows are hidden. It has a default value of `false`.
     */
    val hideOverlayWindows: Boolean get() = false

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
        get() =
            if (BuildConfig.MARVELX_BACKEND_BASE_URL.startsWith("https")) "https" else "http"
    override val host: String get() = BuildConfig.MARVELX_BACKEND_DOMAIN
    override val port: Int
        get() =
            BuildConfig.MARVELX_BACKEND_BASE_URL.split(":").last().toInt()

    override val websocketUrl: String
        get() = BuildConfig.MARVELX_BACKEND_WEBSOCKET_BASE_URL

    override val isAutoLogoutEnabled: Boolean
        get() = false

    override val appBuildType: AppBuildType
        get() = AppBuildType.getType()

    override val packageSignature: String
        get() = "tPwQygNBC+Pl1e3T5qwNGCg/oy9wOM9p+c4oddwuGFE="
}