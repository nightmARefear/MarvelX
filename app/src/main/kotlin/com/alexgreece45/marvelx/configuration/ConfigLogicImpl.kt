package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.model.AppBuildType
import com.alexgreece45.marvelx.core.common.config.model.MarvelApiConfig
import com.alexgreece45.marvelx.core.common.config.model.TimeoutData
import com.alexgreece45.marvelx.core.common.constants.ConstantsContentTypes
import com.alexgreece45.marvelx.util.extensions.getSchemeFromUrl
import org.koin.core.annotation.Single

@Single
internal class ConfigLogicImpl : ConfigLogic {

    override val appBuildType: AppBuildType
        get() = when (BuildConfig.BUILD_TYPE) {
            AppBuildType.DEBUG.key -> AppBuildType.DEBUG
            AppBuildType.MOCK.key -> AppBuildType.MOCK
            AppBuildType.RELEASE.key -> AppBuildType.RELEASE
            else -> AppBuildType.RELEASE
        }

    override val refreshTokenInterval: Long get() = 15L
    override val scheme: String
        get() = BuildConfig.MARVELX_BACKEND_BASE_URL.getSchemeFromUrl()
    override val host: String get() = BuildConfig.MARVELX_BACKEND_DOMAIN

    override val serverUrl: String
        get() = BuildConfig.MARVELX_BACKEND_BASE_URL

    override val marvelApiConfig: MarvelApiConfig
        get() = MarvelApiConfig(
            scheme = BuildConfig.MARVEL_API_BASE_URL.getSchemeFromUrl(),
            host = BuildConfig.MARVEL_API_DOMAIN,
            endpointPrefix = BuildConfig.MARVEL_API_ENDPOINT_PREFIX
        )

    override val backendTimeoutData: TimeoutData
        get() = TimeoutData(
            connectTimeout = 15L,
            readTimeout = 15L
        )

    override val backendMaxAuthenticationTries: Int
        get() = 3

    override val backendEndpointPrefix: String
        get() = BuildConfig.MARVELX_BACKEND_ENDPOINT_PREFIX

    override val backendDomain: String
        get() = BuildConfig.MARVELX_BACKEND_DOMAIN

    override val backendDomainPin: String
        get() = BuildConfig.MARVELX_BACKEND_DOMAIN_PIN

    override val googleServerClientId: String
        get() = BuildConfig.FIREBASE_SERVER_CLIENT_ID

    override val supportedImageFileExtensions: List<String>
        get() = ConstantsContentTypes.ALL_FORMATS

    override val imageFileMaxSize: Long
        get() = 5 * 1024 * 1024 // 5MB
}