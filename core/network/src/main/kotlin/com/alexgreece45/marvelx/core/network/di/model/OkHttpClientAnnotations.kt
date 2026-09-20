package com.alexgreece45.marvelx.core.network.di.model

import org.koin.core.annotation.Named

@Named
internal annotation class BackendOkHttpClient

@Named
internal annotation class RefreshAuthTokenOkHttpClient

/**
 * Public because the websocket connection is owned by the app module's `WebsocketController`,
 * which has to request this exact client (pinning, ping interval, timeouts) from Koin.
 */
@Named
annotation class WebsocketOkHttpClient
