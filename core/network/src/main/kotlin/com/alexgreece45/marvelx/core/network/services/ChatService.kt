package com.alexgreece45.marvelx.core.network.services

import com.alexgreece45.marvelx.core.network.model.api.chat.RequestOpenDirectChat
import com.alexgreece45.marvelx.core.network.model.api.chat.RequestOpenOrderItemChat
import com.alexgreece45.marvelx.core.network.model.api.chat.RequestSendMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseUnreadCount
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ChatService {

    @GET("/api/private/marvelx/chats")
    suspend fun getConversations(): Response<List<ResponseConversation>>

    @GET("/api/private/marvelx/chats/unread-count")
    suspend fun getUnreadCount(): Response<ResponseUnreadCount>

    @POST("/api/private/marvelx/chats/direct")
    suspend fun openDirectConversation(
        @Body body: RequestOpenDirectChat
    ): Response<ResponseConversation>

    @POST("/api/private/marvelx/chats/order-items")
    suspend fun openOrderItemConversation(
        @Body body: RequestOpenOrderItemChat
    ): Response<ResponseConversation>

    @GET("/api/private/marvelx/chats/{conversationId}")
    suspend fun getConversation(
        @Path("conversationId") conversationId: String
    ): Response<ResponseConversation>

    /**
     * Newest first. [beforeMessageId] is a cursor: only messages older than it are returned.
     */
    @GET("/api/private/marvelx/chats/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("before") beforeMessageId: String?,
        @Query("limit") limit: Int
    ): Response<List<ResponseChatMessage>>

    @POST("/api/private/marvelx/chats/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body body: RequestSendMessage
    ): Response<ResponseChatMessage>

    @PATCH("/api/private/marvelx/chats/{conversationId}/read")
    suspend fun markConversationRead(
        @Path("conversationId") conversationId: String
    ): Response<Void>
}
