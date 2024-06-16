package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.models.MarvelApiConfig
import com.alexgreece45.marvelx.util.extensions.getPortFromUrl
import com.alexgreece45.marvelx.util.extensions.getSchemeFromUrl

interface ConfigLogic {
    /**
     * The refresh interval, in minutes, for refreshing the `access` token. Its value is set to `15` by default.
     */
    val refreshTokenInterval: Long

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
     * The client ID for the Google Auth server.
     */
    val googleServerClientId: String

    /**
     * A list of supported image file extensions (e.g., "jpg", "png").
     */
    val supportedImageFileExtensions: List<String>

    /**
     * The maximum allowed size for an image file, in bytes.
     */
    val imageFileMaxSize: Long
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

    override val refreshTokenInterval: Long get() = 15L
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
    override val googleServerClientId: String
        get() = BuildConfig.FIREBASE_SERVER_CLIENT_ID
    override val supportedImageFileExtensions: List<String>
        get() = listOf("jpg", "jpeg", "png")
    override val imageFileMaxSize: Long
        get() = 5 * 8 * 1024 * 1024 // 5MB
}