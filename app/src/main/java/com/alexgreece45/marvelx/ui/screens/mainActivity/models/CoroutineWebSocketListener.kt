package com.alexgreece45.marvelx.ui.screens.mainActivity.models

import kotlinx.coroutines.channels.Channel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * A coroutine implementation of [WebSocketListener] used for asynchronous operations.
 */
class CoroutineWebSocketListener : WebSocketListener() {
    val eventChannel = Channel<WebSocketEvent>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        eventChannel.trySend(WebSocketEvent.OnOpen(response)).isSuccess
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        eventChannel.trySend(WebSocketEvent.OnMessage(text)).isSuccess
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        eventChannel.trySend(WebSocketEvent.OnClosing(code, reason)).isSuccess
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        eventChannel.trySend(WebSocketEvent.OnClosed(code, reason)).isSuccess
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        eventChannel.trySend(WebSocketEvent.OnFailure(t, response)).isSuccess
    }
}