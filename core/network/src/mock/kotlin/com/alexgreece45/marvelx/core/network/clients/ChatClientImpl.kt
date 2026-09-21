package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatOrderItem
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatParticipant
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.ChatService
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.time.Instant
import java.util.UUID

/**
 * In-memory chat backend for the mock flavour. State lives for the process lifetime so sending,
 * reading and reopening conversations behaves like a real server within one session.
 */
@Single
internal class ChatClientImpl(
    @Suppress("unused") private val chatService: ChatService
) : ChatClient {

    private val mutex = Mutex()
    private val conversations = mutableMapOf<String, ResponseConversation>()
    private val messages = mutableMapOf<String, MutableList<ResponseChatMessage>>()

    init {
        seed()
    }

    override suspend fun getConversations(): ApiResult<List<ResponseConversation>> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            ApiResult.Success(conversations.values.sortedByDescending { it.updatedAt })
        }
    }

    override suspend fun getUnreadCount(): ApiResult<ResponseUnreadCount> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            ApiResult.Success(ResponseUnreadCount(conversations.values.sumOf { it.unreadCount }))
        }
    }

    override suspend fun openDirectConversation(userId: String): ApiResult<ResponseConversation> {
        delay(NETWORK_DELAY_MILLIS)
        if (userId == BLOCKED_USER_ID) return ApiResult.Failed(ChatClient.FORBIDDEN_FAILURE)

        return mutex.withLock {
            val existing = conversations.values.firstOrNull {
                it.type == TYPE_DIRECT && it.participant.userId == userId
            }
            ApiResult.Success(
                existing ?: newConversation(
                    type = TYPE_DIRECT,
                    participant = participantFor(userId),
                    orderItem = null
                )
            )
        }
    }

    override suspend fun openOrderItemConversation(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseConversation> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            val existing = conversations.values.firstOrNull {
                it.orderItem?.orderItemId == orderItemId
            }
            ApiResult.Success(
                existing ?: newConversation(
                    type = TYPE_ORDER_ITEM,
                    participant = participantFor(SELLER_USER_ID),
                    orderItem = ResponseChatOrderItem(
                        orderId = orderId,
                        orderItemId = orderItemId,
                        sellerUserId = SELLER_USER_ID,
                        title = "Iron Man Mark 85 Helmet",
                        coverImageUrl = "",
                        price = "129.99"
                    )
                )
            )
        }
    }

    override suspend fun getConversation(conversationId: String): ApiResult<ResponseConversation> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            conversations[conversationId]
                ?.let { ApiResult.Success(it) }
                ?: ApiResult.Failed("Conversation not found.")
        }
    }

    override suspend fun getMessages(
        conversationId: String,
        beforeMessageId: String?,
        limit: Int
    ): ApiResult<List<ResponseChatMessage>> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            val all = messages[conversationId].orEmpty()
            val newestFirst = all.asReversed()
            val startIndex = beforeMessageId
                ?.let { cursor -> newestFirst.indexOfFirst { it.messageId == cursor } + 1 }
                ?.takeIf { it > 0 }
                ?: 0
            ApiResult.Success(newestFirst.drop(startIndex).take(limit))
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        clientMessageId: String,
        body: String
    ): ApiResult<ResponseChatMessage> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            val conversation = conversations[conversationId]
                ?: return ApiResult.Failed("Conversation not found.")

            val existing = messages[conversationId]?.firstOrNull { it.clientMessageId == clientMessageId }
            if (existing != null) return ApiResult.Success(existing)

            val message = ResponseChatMessage(
                messageId = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderUserId = CURRENT_USER_ID,
                body = body,
                sentAt = Instant.now().toString(),
                clientMessageId = clientMessageId
            )
            messages.getOrPut(conversationId) { mutableListOf() }.add(message)
            conversations[conversationId] = conversation.copy(
                lastMessage = message,
                updatedAt = message.sentAt
            )
            ApiResult.Success(message)
        }
    }

    override suspend fun markConversationRead(conversationId: String): ApiResult<String> {
        delay(NETWORK_DELAY_MILLIS)
        return mutex.withLock {
            conversations[conversationId]?.let {
                conversations[conversationId] = it.copy(unreadCount = 0)
            }
            ApiResult.Success("")
        }
    }

    // Seed data.

    private fun seed() {
        val orderChat = newConversation(
            type = TYPE_ORDER_ITEM,
            participant = participantFor(SELLER_USER_ID),
            orderItem = ResponseChatOrderItem(
                orderId = "1",
                orderItemId = "100",
                sellerUserId = SELLER_USER_ID,
                title = "Spider-Man No Way Home Hoodie",
                coverImageUrl = "",
                price = "54.90"
            )
        )
        addSeedMessages(
            orderChat.conversationId,
            listOf(
                CURRENT_USER_ID to "Hi! Has the hoodie been shipped yet?",
                SELLER_USER_ID to "Hey, yes — handed to the courier this morning. You should get a tracking update tonight.",
                SELLER_USER_ID to "Let me know if the size isn't right, replacements are no problem."
            ),
            unread = 2
        )

        val directChat = newConversation(
            type = TYPE_DIRECT,
            participant = participantFor(FRIEND_USER_ID),
            orderItem = null
        )
        addSeedMessages(
            directChat.conversationId,
            listOf(
                FRIEND_USER_ID to "Are you selling the Thor figure from your listings?",
                CURRENT_USER_ID to "Yeah, still available. Want it?"
            ),
            unread = 0
        )
    }

    private fun addSeedMessages(
        conversationId: String,
        bodies: List<Pair<String, String>>,
        unread: Int
    ) {
        var sentAt = Instant.now().minusSeconds(bodies.size * 600L)
        val seeded = bodies.map { (sender, body) ->
            sentAt = sentAt.plusSeconds(600)
            ResponseChatMessage(
                messageId = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderUserId = sender,
                body = body,
                sentAt = sentAt.toString()
            )
        }
        messages[conversationId] = seeded.toMutableList()
        conversations[conversationId] = conversations.getValue(conversationId).copy(
            lastMessage = seeded.last(),
            unreadCount = unread,
            updatedAt = seeded.last().sentAt
        )
    }

    private fun newConversation(
        type: String,
        participant: ResponseChatParticipant,
        orderItem: ResponseChatOrderItem?
    ): ResponseConversation {
        val conversation = ResponseConversation(
            conversationId = UUID.randomUUID().toString(),
            type = type,
            participant = participant,
            orderItem = orderItem,
            lastMessage = null,
            unreadCount = 0,
            updatedAt = Instant.now().toString()
        )
        conversations[conversation.conversationId] = conversation
        messages.putIfAbsent(conversation.conversationId, mutableListOf())
        return conversation
    }

    private fun participantFor(userId: String): ResponseChatParticipant =
        when (userId) {
            SELLER_USER_ID -> ResponseChatParticipant(userId, "Peter", "Parker", null)
            FRIEND_USER_ID -> ResponseChatParticipant(userId, "Natasha", "Romanoff", null)
            else -> ResponseChatParticipant(userId, "User", userId, null)
        }

    private companion object {
        const val NETWORK_DELAY_MILLIS = 300L
        const val TYPE_DIRECT = "direct"
        const val TYPE_ORDER_ITEM = "order_item"
        const val CURRENT_USER_ID = "1"
        const val SELLER_USER_ID = "2"
        const val FRIEND_USER_ID = "3"
        const val BLOCKED_USER_ID = "99"
    }
}
