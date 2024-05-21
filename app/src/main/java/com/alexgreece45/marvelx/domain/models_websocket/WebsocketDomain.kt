package com.alexgreece45.marvelx.domain.models_websocket

import com.alexgreece45.marvelx.controllers.models.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.models.websocket.WebsocketServerResponseType
import com.alexgreece45.marvelx.data.models_websocket.ResponseWebsocket
import java.util.UUID

/**
 * The domain object of a websocket's request response.
 *
 * @param requestId             The request ID of the initial websocket request used to distinguish
 * @param websocketRequestType  The initial websocket request of the application.
 * the requests.
 * @param serverResponseType        The server response type defining whether the data requested was
 * successfully fetched.
 * @param data                  The server response defining the requested data.
 */
data class WebsocketDomain(
    val requestId: UUID,
    val websocketRequestType: WebsocketRequestType,
    val serverResponseType: WebsocketServerResponseType,
    val data: String
)

/**
 * Converts this [ResponseWebsocket] to a [WebsocketDomain].
 */
fun ResponseWebsocket.toWebsocketDomain() =
    WebsocketDomain(
        websocketRequestType = WebsocketRequestType.of(this.clientRequestType),
        requestId = UUID.fromString(this.clientRequestId),
        serverResponseType = WebsocketServerResponseType.of(this.serverResponseType),
        data = this.data
    )