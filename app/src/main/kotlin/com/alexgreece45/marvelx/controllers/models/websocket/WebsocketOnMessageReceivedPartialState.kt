package com.alexgreece45.marvelx.controllers.models.websocket

import com.alexgreece45.marvelx.domain.models_websocket.WebsocketDomain

/**
 * Used by the websocket listener for the onMessage event.
 */
sealed interface WebsocketOnMessageReceivedPartialState {
    data class Success(val websocketDomain: WebsocketDomain) : WebsocketOnMessageReceivedPartialState
    data class Failed(val errorMessage: String) : WebsocketOnMessageReceivedPartialState
}