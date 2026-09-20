package com.alexgreece45.marvelx.core.network.model.api.chat

import com.google.gson.annotations.SerializedName

/**
 * A conversation as seen by the requesting user: [participant] is always the *other* user.
 * [orderItem] is present only when [type] is `order_item`.
 */
data class ResponseConversation(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("type") val type: String,
    @SerializedName("participant") val participant: ResponseChatParticipant,
    @SerializedName("orderItem") val orderItem: ResponseChatOrderItem? = null,
    @SerializedName("lastMessage") val lastMessage: ResponseChatMessage? = null,
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("updatedAt") val updatedAt: String
)

data class ResponseChatParticipant(
    @SerializedName("userId") val userId: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null
)

data class ResponseChatOrderItem(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("orderItemId") val orderItemId: String,
    @SerializedName("sellerUserId") val sellerUserId: String,
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("price") val price: String
)

data class ResponseUnreadCount(
    @SerializedName("unreadCount") val unreadCount: Int
)

data class RequestOpenDirectChat(
    @SerializedName("userId") val userId: String
)

data class RequestOpenOrderItemChat(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("orderItemId") val orderItemId: String
)

data class RequestSendMessage(
    @SerializedName("clientMessageId") val clientMessageId: String,
    @SerializedName("body") val body: String
)
