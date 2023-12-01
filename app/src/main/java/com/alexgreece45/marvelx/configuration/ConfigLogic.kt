package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.TOKEN_CLIENT_ID
import com.alexgreece45.marvelx.util.TOKEN_REDIRECT_URI
import javax.inject.Inject
import javax.inject.Singleton

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
     * The app's build type (i.e., debug, mock, or release).
     */
    val appBuildType: AppBuildType

    /**
     * The package signature of this application.
     */
    val packageSignature: String
}

enum class AppBuildType {
    DEBUG, MOCK, RELEASE;

    companion object {
        fun getType(): AppBuildType =
            when (BuildConfig.BUILD_TYPE) {
                "debug" -> DEBUG
                "mock" -> MOCK
                "release" -> RELEASE
                else -> RELEASE
            }
    }
}

@Singleton
class ConfigLogicImpl @Inject constructor() : ConfigLogic {

    override val clientId: String get() = TOKEN_CLIENT_ID
    override val clientSecret: String get() = BuildConfig.TOKEN_CLIENT_SECRET
    override val refreshTokenInterval: Long get() = 15L
    override val uri: String get() = TOKEN_REDIRECT_URI
    override val scheme: String
        get() =
            if (BuildConfig.MARVELX_BACKEND_BASE_URL.startsWith("https")) "https" else "http"
    override val host: String get() = BuildConfig.MARVELX_BACKEND_DOMAIN
    override val port: Int
        get() =
            BuildConfig.MARVELX_BACKEND_BASE_URL.split(":").last().toInt()
    override val appBuildType: AppBuildType
        get() = AppBuildType.getType()
    override val packageSignature: String
        get() = "K9sqm8e3aPgpA8Vxmp/D+M4oJEU="
}