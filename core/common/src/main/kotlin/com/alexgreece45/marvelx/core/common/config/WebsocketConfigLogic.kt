package com.alexgreece45.marvelx.core.common.config

import com.alexgreece45.marvelx.core.common.config.model.TimeoutData

interface WebsocketConfigLogic {

    /**
     * The websocket URL.
     */
    val websocketUrl: String

    /**
     * The timeout data for the websocket.
     */
    val timeoutData: TimeoutData

    val domain: String
    val domainPin: String
}