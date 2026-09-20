package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.chat.RequestOpenDirectChat
import com.alexgreece45.marvelx.core.network.model.api.chat.RequestOpenOrderItemChat
import com.alexgreece45.marvelx.core.network.model.api.chat.RequestSendMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseChatMessage
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation
import com.alexgreece45.marvelx.core.network.model.api.chat.ResponseUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.ChatService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCallNoTransformation
import okio.IOException
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
internal class ChatClientImpl(
    private val chatService: ChatService
) : ChatClient {

    override suspend fun getConversations(): ApiResult<List<ResponseConversation>> =
        executeApiServiceCall { chatService.getConversations() }

    override suspend fun getUnreadCount(): ApiResult<ResponseUnreadCount> =
        executeApiServiceCall { chatService.getUnreadCount() }

    override suspend fun openDirectConversation(userId: String): ApiResult<ResponseConversation> =
        executeOpenConversationCall {
            chatService.openDirectConversation(RequestOpenDirectChat(userId = userId))
        }

    override suspend fun openOrderItemConversation(
        orderId: String,
        orderItemId: String
    ): ApiResult<ResponseConversation> =
        executeOpenConversationCall {
            chatService.openOrderItemConversation(
                RequestOpenOrderItemChat(orderId = orderId, orderItemId = orderItemId)
            )
        }

    override suspend fun getConversation(conversationId: String): ApiResult<ResponseConversation> =
        executeApiServiceCall { chatService.getConversation(conversationId) }

    override suspend fun getMessages(
        conversationId: String,
        beforeMessageId: String?,
        limit: Int
    ): ApiResult<List<ResponseChatMessage>> =
        executeApiServiceCall { chatService.getMessages(conversationId, beforeMessageId, limit) }

    override suspend fun sendMessage(
        conversationId: String,
        clientMessageId: String,
        body: String
    ): ApiResult<ResponseChatMessage> =
        executeApiServiceCall {
            chatService.sendMessage(
                conversationId = conversationId,
                body = RequestSendMessage(clientMessageId = clientMessageId, body = body)
            )
        }

    override suspend fun markConversationRead(conversationId: String): ApiResult<String> =
        executeApiServiceCallNoTransformation { chatService.markConversationRead(conversationId) }

    /**
     * Like [executeApiServiceCall] but surfaces a 403 as [ChatClient.FORBIDDEN_FAILURE], because
     * "blocked" needs a different message on screen than a generic failure.
     */
    private suspend fun executeOpenConversationCall(
        apiCall: suspend () -> Response<ResponseConversation>
    ): ApiResult<ResponseConversation> =
        try {
            val response = apiCall()
            when {
                response.isSuccessful -> response.body()
                    ?.let { ApiResult.Success(it) }
                    ?: ApiResult.Failed(response.errorBody().toString())

                response.code() == HTTP_FORBIDDEN -> ApiResult.Failed(ChatClient.FORBIDDEN_FAILURE)

                else -> ApiResult.Failed(response.errorBody().toString())
            }
        } catch (_: IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage.orEmpty())
        }

    private companion object {
        const val HTTP_FORBIDDEN = 403
    }
}
