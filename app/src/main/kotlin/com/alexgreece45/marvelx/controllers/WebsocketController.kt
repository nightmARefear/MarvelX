package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketOnMessageReceivedPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState.Failed
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState.Success
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketTerminationCode
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.models_websocket.RequestWebsocket
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import kotlin.coroutines.CoroutineContext


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

    /**
     * Sends the given [websocketRequestType] to the websocket server, and returns the result.
     *
     * @param websocketRequestType  The websocket request to be sent.
     */
    suspend fun sendRequest(websocketRequestType: WebsocketRequestType): Flow<WebsocketSendRequestPartialState>

    /**
     * This function is invoked when a websocket message has been received.
     */
    suspend fun onMessageReceived(websocketOnMessageReceivedPartialState: WebsocketOnMessageReceivedPartialState)
}

class WebsocketControllerImpl(
    private val configLogic: ConfigLogic,
    private val okHttpClient: OkHttpClient,
    private val authController: AuthController,
    private val appLogger: AppLogger,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : WebsocketController {
    /**
     * The websocket client listener.
     */
    private lateinit var webSocketListener: WebSocketListener

    /**
     * The websocket client instance.
     */
    private var webSocket: WebSocket? = null

    private val websocketReceivedMessage: MutableSharedFlow<WebsocketOnMessageReceivedPartialState> =
        MutableSharedFlow()

    override fun init() {
        return // TODO: To be reviewed in the future.

        // Ensures that the websocket client doesn't get re-initialized.
        if (webSocket != null) return

        val request = Request.Builder()
            .url(configLogic.websocketUrl)
            .addHeader(
                name = ConstantsSecurity.HeaderName.UserAgent.name,
                value = ConstantsSecurity.HeaderValue.APP_USER_AGENT
            )
            .addHeader(
                name = ConstantsSecurity.HeaderName.Authorization.name,
                value = authController.getRefreshToken().orEmpty()
            )
            .build()

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
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
            appLogger.d("WebsocketController") { "The websocket connection has been terminated." }
        }
    }

    override suspend fun sendRequest(websocketRequestType: WebsocketRequestType): Flow<WebsocketSendRequestPartialState> =
        flow {
            val requestId = UUID.randomUUID()
            val requestWebsocket = RequestWebsocket(
                requestId = requestId,
                requestType = websocketRequestType
            )
            sendMessage(gson.toJson(requestWebsocket))

            coroutineScope {
                websocketReceivedMessage.collect {
                    when (it) {
                        is WebsocketOnMessageReceivedPartialState.Failed -> {
                            emit(
                                Failed(
                                    errorMessage = it.errorMessage
                                )
                            )
                        }

                        is WebsocketOnMessageReceivedPartialState.Success -> {
                            if (it.websocketDomain.requestId == requestId) {
                                emit(
                                    Success(
                                        websocketDomain = it.websocketDomain
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    override suspend fun onMessageReceived(websocketOnMessageReceivedPartialState: WebsocketOnMessageReceivedPartialState) {
        // Emit websocket message.
        websocketReceivedMessage.emit(websocketOnMessageReceivedPartialState)
    }

    /**
     * Sends the given [message] to the websocket server.
     *
     * @param message   The request to be sent.
     */
    private fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}
