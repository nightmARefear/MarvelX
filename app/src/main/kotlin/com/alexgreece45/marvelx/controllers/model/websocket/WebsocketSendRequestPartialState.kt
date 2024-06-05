package com.alexgreece45.marvelx.controllers.model.websocket

import com.alexgreece45.marvelx.domain.model_websocket.WebsocketDomain

sealed interface WebsocketSendRequestPartialState {
    data class Success(val websocketDomain: WebsocketDomain) : WebsocketSendRequestPartialState
    data class Failed(val errorMessage: String) : WebsocketSendRequestPartialState
}