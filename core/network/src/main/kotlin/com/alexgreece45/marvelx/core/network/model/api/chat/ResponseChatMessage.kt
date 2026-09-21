package com.alexgreece45.marvelx.core.network.model.api.chat

import com.google.gson.annotations.SerializedName

/**
 * A single chat message. Returned by the REST message endpoints and carried verbatim as the
 * payload of the `chat.message` websocket frame.
 */
data class ResponseChatMessage(
    @SerializedName("messageId") val messageId: String,
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("senderUserId") val senderUserId: String,
    @SerializedName("body") val body: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("clientMessageId") val clientMessageId: String? = null
)
