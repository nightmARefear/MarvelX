package com.alexgreece45.marvelx.core.network.model.websocket

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * Every websocket frame, in both directions, is one of these. [type] routes the frame and
 * [payload] is decoded into the matching payload class by the receiver.
 */
data class WebsocketEnvelope(
    @SerializedName("type") val type: String,
    @SerializedName("payload") val payload: JsonElement? = null
)

object WebsocketFrameType {
    // Server -> client.
    const val CHAT_MESSAGE = "chat.message"
    const val CHAT_READ = "chat.read"
    const val NOTIFICATION_DELETE = "notification.delete"
    const val ERROR = "error"
    const val TIMESTAMP_RESULT = "timestamp.result"

    // Client -> server.
    const val TIMESTAMP_GET = "timestamp.get"
}

object WebsocketErrorCode {
    const val UNAUTHORIZED = "unauthorized"
}
