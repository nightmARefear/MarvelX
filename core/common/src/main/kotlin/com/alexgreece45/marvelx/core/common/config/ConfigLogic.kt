package com.alexgreece45.marvelx.core.common.config

import com.alexgreece45.marvelx.core.common.config.model.AppBuildType
import com.alexgreece45.marvelx.core.common.config.model.MarvelApiConfig
import com.alexgreece45.marvelx.core.common.config.model.TimeoutData

interface ConfigLogic {

    /**
     * The app's build type (i.e., debug, mock, or release).
     */
    val appBuildType: AppBuildType

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
     * The MarvelX backend URL.
     */
    val serverUrl: String

    /**
     * The network config for the Marvel API.
     */
    val marvelApiConfig: MarvelApiConfig

    /**
     * The timeout data for the MarvelX backend.
     */
    val backendTimeoutData: TimeoutData

    /**
     * The maximum number of authentication tries for the marvelx backend.
     */
    val backendMaxAuthenticationTries: Int

    val backendEndpointPrefix: String

    val backendDomain: String

    val backendDomainPin: String

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