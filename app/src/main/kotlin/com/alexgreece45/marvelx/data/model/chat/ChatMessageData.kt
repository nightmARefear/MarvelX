package com.alexgreece45.marvelx.data.model.chat

import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import java.time.Instant

enum class ChatMessageStatusData {
    /** Optimistically shown; the server has not confirmed it yet. */
    SENDING,
    SENT,

    /** The send failed; the user can retry. */
    FAILED
}

data class ChatMessageData(
    val messageId: String,
    val conversationId: String,
    val senderUserId: String,
    val body: String,
    val sentAt: Instant,
    val clientMessageId: String?,
    val status: ChatMessageStatusData
) {
    /**
     * Two messages are the same message when the server id matches, or — before the server has
     * assigned one — when they share the client id that was used to send them.
     */
    fun isSameAs(other: ChatMessageData): Boolean =
        messageId == other.messageId ||
                (clientMessageId != null && clientMessageId == other.clientMessageId)
}

fun ResponseChatMessage.toData(): ChatMessageData =
    ChatMessageData(
        messageId = messageId,
        conversationId = conversationId,
        senderUserId = senderUserId,
        body = body,
        sentAt = Instant.parse(sentAt),
        clientMessageId = clientMessageId,
        status = ChatMessageStatusData.SENT
    )
