package com.alexgreece45.marvelx.ui.screens.mainActivity.model

sealed class WebSocketEvent {
    data class OnOpen(val response: okhttp3.Response) : WebSocketEvent()
    data class OnMessage(val text: String) : WebSocketEvent()
    data class OnClosing(val code: Int, val reason: String) : WebSocketEvent()
    data class OnClosed(val code: Int, val reason: String) : WebSocketEvent()
    data class OnFailure(val t: Throwable, val response: okhttp3.Response?) : WebSocketEvent()
}