package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.core.common.config.WebsocketConfigLogic
import com.alexgreece45.marvelx.core.common.config.model.TimeoutData
import org.koin.core.annotation.Single

@Single
internal class WebsocketConfigLogicImpl : WebsocketConfigLogic {

    override val websocketUrl: String get() = BuildConfig.MARVELX_BACKEND_WEBSOCKET_BASE_URL

    override val timeoutData: TimeoutData
        get() = TimeoutData(
            connectTimeout = 10L,
            readTimeout = 10L
        )

    override val domain: String get() = BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN

    override val domainPin: String get() = BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN_PIN
}