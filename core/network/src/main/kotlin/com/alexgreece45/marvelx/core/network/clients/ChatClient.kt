package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult

interface ChatClient {

    suspend fun getConversations(): ApiResult<List<ResponseConversation>>

    suspend fun getUnreadCount(): ApiResult<ResponseUnreadCount>

    /**
     * Get-or-create the single direct conversation with [userId]. Fails with
     * [FORBIDDEN_FAILURE] when either user has blocked the other.
     */
    suspend fun openDirectConversation(userId: String): ApiResult<ResponseConversation>

    /**
     * Get-or-create the single conversation for an order item. The server resolves buyer and
     * seller itself; the caller must be one of them or the call fails with [FORBIDDEN_FAILURE].
     */
    suspend fun openOrderItemConversation(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseConversation>

    suspend fun getConversation(conversationId: String): ApiResult<ResponseConversation>

    suspend fun getMessages(
        conversationId: String,
        beforeMessageId: String?,
        limit: Int
    ): ApiResult<List<ResponseChatMessage>>

    suspend fun sendMessage(
        conversationId: String,
        clientMessageId: String,
        body: String
    ): ApiResult<ResponseChatMessage>

    suspend fun markConversationRead(conversationId: String): ApiResult<String>

    companion object {
        /**
         * Failure message used when the server answers 403, so callers can distinguish "you are
         * not allowed to talk to this user" from a generic failure.
         */
        const val FORBIDDEN_FAILURE = "chat_forbidden"
    }
}
