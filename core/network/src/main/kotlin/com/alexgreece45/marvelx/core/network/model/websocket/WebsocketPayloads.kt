package com.alexgreece45.marvelx.core.network.model.websocket

import com.google.gson.annotations.SerializedName

data class WebsocketChatReadPayload(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("readByUserId") val readByUserId: String,
    @SerializedName("lastReadMessageId") val lastReadMessageId: String? = null
)

data class WebsocketNotificationDeletePayload(
    @SerializedName("notificationId") val notificationId: Int
)

data class WebsocketErrorPayload(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String? = null
)

data class WebsocketTimestampRequestPayload(
    @SerializedName("requestId") val requestId: String
)

data class WebsocketTimestampResultPayload(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("timestamp") val timestamp: String
)
