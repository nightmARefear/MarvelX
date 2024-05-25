package com.alexgreece45.marvelx.data.models_websocket

import com.alexgreece45.marvelx.controllers.models.websocket.WebsocketRequestType
import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * This class defines a request to the websocket server.
 *
 * @param requestId     The UUID of this websocket request.
 * @param requestType   The type of this websocket request.
 */
data class RequestWebsocket(
    @SerializedName("requestId") val requestId: UUID,
    @SerializedName("requestType") val requestType: WebsocketRequestType
)
