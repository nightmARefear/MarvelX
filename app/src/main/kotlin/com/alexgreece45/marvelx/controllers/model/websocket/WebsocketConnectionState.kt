package com.alexgreece45.marvelx.controllers.model.websocket

enum class WebsocketConnectionState {
    Disconnected,
    Connecting,
    Connected,

    /**
     * The connection dropped and a reconnection attempt is scheduled.
     */
    Reconnecting;

    val isConnected: Boolean get() = this == Connected
}
