package com.alexgreece45.marvelx.controllers.model.websocket

/**
 * Defines a websocket server response; either [Success] if the requested data was successfully
 * fetched, otherwise [Failed].
 */
sealed class WebsocketServerResponseType(val key: String) {
    companion object {
        private fun asList(): List<WebsocketServerResponseType> = listOf(Success, Failed)

        fun of(key: String): WebsocketServerResponseType = asList().firstOrNull { it.key == key } ?: Failed
    }

    data object Success : WebsocketServerResponseType(key = "success")
    data object Failed : WebsocketServerResponseType(key = "failed")
}