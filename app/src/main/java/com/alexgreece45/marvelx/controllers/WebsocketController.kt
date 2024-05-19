package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.models.websocket.WebsocketTerminationCode
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener


/**
 * A websocket controller that provides various utility functionalities such as initializing the
 * websocket connection, sending messages, and more.
 */
interface WebsocketController {
    /**
     * Initializes / opens the websocket connection.
     */
    fun init()

    /**
     * Sets the websocket listener to the given [webSocketListener].
     */
    fun setWebSocketListener(webSocketListener: WebSocketListener)

    /**
     * Closes the websocket connection.
     */
    fun close()
}

class WebsocketControllerImpl(
    private val configLogic: ConfigLogic,
    private val okHttpClient: OkHttpClient,
    private val authController: AuthController,
    private val appLoggerController: AppLoggerController
) : WebsocketController {
    private lateinit var webSocketListener: WebSocketListener
    private var webSocket: WebSocket? = null

    override fun init() {
        // Ensures that the websocket doesn't get re-initialized.
        if (webSocket != null) return

        val request = Request.Builder()
            .url(configLogic.websocketUrl)
            .addHeader(
                name = ConstantsSecurity.HeaderNames.UserAgent.name,
                value = ConstantsSecurity.HeaderValues.APP_USER_AGENT
            )
            .addHeader(
                name = ConstantsSecurity.HeaderNames.Authorization.name,
                value = authController.getRefreshToken().orEmpty()
            )
            .build()

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
        appLoggerController.d(
            "WebsocketController"
        ) { "The websocket connection has been opened." }
    }

    override fun setWebSocketListener(webSocketListener: WebSocketListener) {
        this.webSocketListener = webSocketListener
    }

    override fun close() {
        val didWebsocketCloseGracefully =
            webSocket?.close(WebsocketTerminationCode.Client.code, null)
        webSocket = null

        // This check is important so as to print the log only when the websocket close gracefully
        // -- i.e., it wasn't already closed or in the process of closing.
        if (didWebsocketCloseGracefully != null && didWebsocketCloseGracefully) {
            appLoggerController.d("WebsocketController") { "The websocket connection has been terminated." }
        }
    }

    /**
     * Sends [message] to the websocket server.
     *
     * @param message   The message to be sent.
     */
    private fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}