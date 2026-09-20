package com.alexgreece45.marvelx.controllers.model.websocket

import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import java.time.Instant

/**
 * A frame pushed by the websocket server, already routed by its `type` and decoded.
 */
sealed interface WebsocketInbound {

    data class ChatMessage(val message: ResponseChatMessage) : WebsocketInbound

    data class ChatRead(
        val conversationId: String,
        val readByUserId: String,
        val lastReadMessageId: String?
    ) : WebsocketInbound

    data class NotificationDeleted(val notificationId: Int) : WebsocketInbound

    data class TimestampResult(val requestId: String, val timestamp: Instant) : WebsocketInbound

    data class Error(val code: String, val message: String?) : WebsocketInbound

    data class Unknown(val type: String) : WebsocketInbound
}
