package com.alexgreece45.marvelx.data.model.chat

import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatOrderItem
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatParticipant
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation
import java.math.BigDecimal
import java.time.Instant

enum class ConversationTypeData(val key: String) {
    DIRECT("direct"),
    ORDER_ITEM("order_item");

    companion object {
        fun fromKey(key: String): ConversationTypeData =
            entries.firstOrNull { it.key == key }
                ?: throw IllegalArgumentException("Unknown conversation type '$key'.")
    }
}

data class ChatParticipantData(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String?
) {
    val fullName: String get() = "$firstName $lastName"
}

data class ChatOrderItemData(
    val orderId: String,
    val orderItemId: String,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val price: BigDecimal
)

data class ConversationData(
    val conversationId: String,
    val type: ConversationTypeData,
    val participant: ChatParticipantData,
    val orderItem: ChatOrderItemData?,
    val lastMessage: ChatMessageData?,
    val unreadCount: Int,
    val updatedAt: Instant
)

fun ResponseConversation.toData(): ConversationData =
    ConversationData(
        conversationId = conversationId,
        type = ConversationTypeData.fromKey(type),
        participant = participant.toData(),
        orderItem = orderItem?.toData(),
        lastMessage = lastMessage?.toData(),
        unreadCount = unreadCount,
        updatedAt = Instant.parse(updatedAt)
    )

fun ResponseChatParticipant.toData(): ChatParticipantData =
    ChatParticipantData(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl
    )

fun ResponseChatOrderItem.toData(): ChatOrderItemData =
    ChatOrderItemData(
        orderId = orderId,
        orderItemId = orderItemId,
        sellerUserId = sellerUserId,
        title = title,
        coverImageUrl = coverImageUrl,
        price = price.toBigDecimal()
    )
