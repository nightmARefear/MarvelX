package com.alexgreece45.marvelx.network_logic.models_websocket

import com.google.gson.annotations.SerializedName

/**
 * The response of a websocket request.
 *
 * @param clientRequestId           The initial request ID of the client (i.e., this application). This
 * is used to distinguish between the websocket requests.
 * @param clientRequestType         The initial websocket request of the client (i.e., this application).
 * @param serverResponseType        The server response type defining whether the data requested was
 * successfully fetched.
 * @param data                      The server response defining the requested data.
 */
data class ResponseWebsocket(
    @SerializedName("clientRequestId") val clientRequestId: String,
    @SerializedName("clientRequestType") val clientRequestType: String,
    @SerializedName("serverResponseType") val serverResponseType: String,
    @SerializedName("data") val data: String
)