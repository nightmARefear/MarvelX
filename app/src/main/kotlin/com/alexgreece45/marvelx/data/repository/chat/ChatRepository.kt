package com.alexgreece45.marvelx.data.repository.chat

import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketConnectionState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketInbound
import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.clients.ChatClient
import com.alexgreece45.marvelx.data.model.chat.ChatMessageData
import com.alexgreece45.marvelx.data.model.chat.ChatMessageStatusData
import com.alexgreece45.marvelx.data.model.chat.ConversationData
import com.alexgreece45.marvelx.data.model.chat.toData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import java.time.Instant
import java.util.UUID
import kotlin.coroutines.CoroutineContext

sealed interface OpenConversationResult {
    data class Success(val conversation: ConversationData) : OpenConversationResult

    /** The server refused: one of the two users has blocked the other. */
    data object Forbidden : OpenConversationResult
    data class Failed(val failureMessage: String) : OpenConversationResult
    data object NetworkError : OpenConversationResult
}

/**
 * Chat state for the whole app. REST is the source of truth for reads and sends; the websocket
 * only pushes deltas, which are folded into the in-memory caches exposed as flows below.
 */
interface ChatRepository {

    /** Every conversation of the current user, newest activity first. */
    val conversations: StateFlow<List<ConversationData>>

    /** Messages per conversation, oldest first. Only populated once a conversation is loaded. */
    val messagesByConversation: StateFlow<Map<String, List<ChatMessageData>>>

    /** Unread messages across all conversations, for the inbox badge. */
    val unreadCount: StateFlow<Int>

    suspend fun refreshConversations(): RepositoryResult<List<ConversationData>>
    suspend fun refreshUnreadCount()

    suspend fun openDirectConversation(userId: String): OpenConversationResult
    suspend fun openOrderItemConversation(orderId: String, orderItemId: String): OpenConversationResult
    suspend fun getConversation(conversationId: String): RepositoryResult<ConversationData>

    /** Replaces the cached messages with the latest page. */
    suspend fun loadLatestMessages(conversationId: String): RepositoryResult<List<ChatMessageData>>

    /** Prepends the page before [beforeMessageId]; an empty page means history is exhausted. */
    suspend fun loadOlderMessages(
        conversationId: String,
        beforeMessageId: String
    ): RepositoryResult<List<ChatMessageData>>

    /** Appends optimistically, then confirms with the server. Failures leave a retryable message. */
    suspend fun sendMessage(conversationId: String, body: String)
    suspend fun retryMessage(conversationId: String, clientMessageId: String)

    suspend fun markConversationRead(conversationId: String): Boolean

    /**
     * Tells the repository which conversation is on screen, so its incoming messages are marked
     * read immediately instead of counting as unread.
     */
    fun setOpenConversation(conversationId: String?)
}

@Single
internal class ChatRepositoryImpl(
    private val chatClient: ChatClient,
    private val websocketController: WebsocketController,
    private val userSessionRepository: UserSessionRepository,
    private val appLogger: AppLogger,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : ChatRepository {

    private val loggerTag = "ChatRepository"

    private val _conversations = MutableStateFlow<List<ConversationData>>(emptyList())
    override val conversations: StateFlow<List<ConversationData>> = _conversations.asStateFlow()

    private val _messagesByConversation = MutableStateFlow<Map<String, List<ChatMessageData>>>(emptyMap())
    override val messagesByConversation: StateFlow<Map<String, List<ChatMessageData>>> =
        _messagesByConversation.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val mutex = Mutex()
    private var openConversationId: String? = null

    init {
        applicationScope.launch {
            websocketController.inbound.collect { inbound ->
                when (inbound) {
                    is WebsocketInbound.ChatMessage -> onIncomingMessage(inbound.message.toData())
                    is WebsocketInbound.ChatRead,
                    is WebsocketInbound.NotificationDeleted,
                    is WebsocketInbound.TimestampResult,
                    is WebsocketInbound.Error,
                    is WebsocketInbound.Unknown -> Unit
                }
            }
        }

        // Anything pushed while the socket was down is missed, so resync on every reconnection.
        applicationScope.launch {
            var wasConnected = false
            websocketController.connectionState.collect { state ->
                val isConnected = state == WebsocketConnectionState.Connected
                if (isConnected && !wasConnected) resyncAfterReconnect()
                wasConnected = isConnected
            }
        }
    }

    // Reads.

    override suspend fun refreshConversations(): RepositoryResult<List<ConversationData>> {
        val result = handleApiResultNoFlow(
            apiCall = chatClient::getConversations,
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success) {
            mutex.withLock {
                _conversations.value = result.data.sortedByDescending { it.updatedAt }
                _unreadCount.value = result.data.sumOf { it.unreadCount }
            }
        }
        return result
    }

    override suspend fun refreshUnreadCount() {
        val result = handleApiResultNoFlow(
            apiCall = chatClient::getUnreadCount,
            transformation = { unreadCount },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success) _unreadCount.value = result.data
    }

    override suspend fun openDirectConversation(userId: String): OpenConversationResult =
        openConversation { chatClient.openDirectConversation(userId) }

    override suspend fun openOrderItemConversation(
        orderId: String,
        orderItemId: String
    ): OpenConversationResult =
        openConversation { chatClient.openOrderItemConversation(orderId, orderItemId) }

    override suspend fun getConversation(conversationId: String): RepositoryResult<ConversationData> {
        val result = handleApiResultNoFlow(
            apiCall = { chatClient.getConversation(conversationId) },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success) upsertConversation(result.data)
        return result
    }

    override suspend fun loadLatestMessages(conversationId: String): RepositoryResult<List<ChatMessageData>> {
        val result = handleApiResultNoFlow(
            apiCall = { chatClient.getMessages(conversationId, beforeMessageId = null, limit = PAGE_SIZE) },
            transformation = { this.map { it.toData() }.sortedBy { it.sentAt } },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success) {
            mutex.withLock {
                // Keep any message still in flight so an optimistic send isn't wiped by a refresh.
                val inFlight = _messagesByConversation.value[conversationId].orEmpty()
                    .filter { it.status != ChatMessageStatusData.SENT }
                setMessagesLocked(conversationId, (result.data + inFlight).sortedBy { it.sentAt })
            }
        }
        return result
    }

    override suspend fun loadOlderMessages(
        conversationId: String,
        beforeMessageId: String
    ): RepositoryResult<List<ChatMessageData>> {
        val result = handleApiResultNoFlow(
            apiCall = { chatClient.getMessages(conversationId, beforeMessageId, PAGE_SIZE) },
            transformation = { this.map { it.toData() }.sortedBy { it.sentAt } },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success && result.data.isNotEmpty()) {
            mutex.withLock {
                val current = _messagesByConversation.value[conversationId].orEmpty()
                val older = result.data.filter { page -> current.none { it.isSameAs(page) } }
                setMessagesLocked(conversationId, older + current)
            }
        }
        return result
    }

    // Writes.

    override suspend fun sendMessage(conversationId: String, body: String) {
        val trimmed = body.trim()
        if (trimmed.isEmpty()) return

        val pending = ChatMessageData(
            messageId = "",
            conversationId = conversationId,
            senderUserId = userSessionRepository.getUserId(),
            body = trimmed,
            sentAt = Instant.now(),
            clientMessageId = UUID.randomUUID().toString(),
            status = ChatMessageStatusData.SENDING
        )
        mutex.withLock { upsertMessageLocked(pending) }
        deliver(pending)
    }

    override suspend fun retryMessage(conversationId: String, clientMessageId: String) {
        val failed = mutex.withLock {
            _messagesByConversation.value[conversationId]
                ?.firstOrNull { it.clientMessageId == clientMessageId && it.status == ChatMessageStatusData.FAILED }
                ?.copy(status = ChatMessageStatusData.SENDING, sentAt = Instant.now())
                ?.also { upsertMessageLocked(it) }
        } ?: return
        deliver(failed)
    }

    override suspend fun markConversationRead(conversationId: String): Boolean {
        val unreadInConversation = _conversations.value
            .firstOrNull { it.conversationId == conversationId }?.unreadCount ?: 0

        val wasMarked = handleApiResultNoTransformationNoFlow(
            apiCall = { chatClient.markConversationRead(conversationId) },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success

        if (wasMarked && unreadInConversation > 0) {
            mutex.withLock {
                _conversations.update { list ->
                    list.map { if (it.conversationId == conversationId) it.copy(unreadCount = 0) else it }
                }
                _unreadCount.update { (it - unreadInConversation).coerceAtLeast(0) }
            }
        }
        return wasMarked
    }

    override fun setOpenConversation(conversationId: String?) {
        openConversationId = conversationId
    }

    // Internals.

    private suspend fun openConversation(
        apiCall: suspend () -> com.alexgreece45.marvelx.core.network.model.generic.ApiResult<
                com.alexgreece45.marvelx.core.network.model.api.chat.ResponseConversation>
    ): OpenConversationResult =
        when (val result = handleApiResultNoFlow(
            apiCall = apiCall,
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )) {
            is RepositoryResult.Success -> {
                upsertConversation(result.data)
                OpenConversationResult.Success(result.data)
            }

            is RepositoryResult.Failed ->
                if (result.failureMessage == ChatClient.FORBIDDEN_FAILURE) {
                    OpenConversationResult.Forbidden
                } else {
                    OpenConversationResult.Failed(result.failureMessage)
                }

            RepositoryResult.NetworkError -> OpenConversationResult.NetworkError
        }

    private suspend fun deliver(message: ChatMessageData) {
        val result = handleApiResultNoFlow(
            apiCall = {
                chatClient.sendMessage(
                    conversationId = message.conversationId,
                    clientMessageId = requireNotNull(message.clientMessageId),
                    body = message.body
                )
            },
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        )

        mutex.withLock {
            when (result) {
                is RepositoryResult.Success -> {
                    upsertMessageLocked(result.data)
                    bumpConversationLocked(result.data, incrementUnread = false)
                }

                is RepositoryResult.Failed,
                RepositoryResult.NetworkError -> {
                    upsertMessageLocked(message.copy(status = ChatMessageStatusData.FAILED))
                    appLogger.e(loggerTag) { "Sending message ${message.clientMessageId} failed: $result" }
                }
            }
        }
    }

    private suspend fun onIncomingMessage(message: ChatMessageData) {
        val isFromMe = message.senderUserId == userSessionRepository.getUserId()
        val isOnScreen = openConversationId == message.conversationId

        mutex.withLock {
            upsertMessageLocked(message)
            bumpConversationLocked(message, incrementUnread = !isFromMe && !isOnScreen)
        }

        if (!isFromMe && isOnScreen) markConversationRead(message.conversationId)

        // A message for a conversation we have never seen: fetch it so the inbox can show it.
        if (_conversations.value.none { it.conversationId == message.conversationId }) {
            getConversation(message.conversationId)
        }
    }

    private suspend fun resyncAfterReconnect() {
        refreshConversations()
        openConversationId?.let { loadLatestMessages(it) }
    }

    private suspend fun upsertConversation(conversation: ConversationData) {
        mutex.withLock {
            _conversations.update { list ->
                (list.filterNot { it.conversationId == conversation.conversationId } + conversation)
                    .sortedByDescending { it.updatedAt }
            }
        }
    }

    private fun upsertMessageLocked(message: ChatMessageData) {
        _messagesByConversation.update { byConversation ->
            val current = byConversation[message.conversationId].orEmpty()
            val merged = if (current.any { it.isSameAs(message) }) {
                current.map { if (it.isSameAs(message)) message else it }
            } else {
                current + message
            }
            byConversation + (message.conversationId to merged.sortedBy { it.sentAt })
        }
    }

    private fun setMessagesLocked(conversationId: String, messages: List<ChatMessageData>) {
        _messagesByConversation.update { it + (conversationId to messages) }
    }

    private fun bumpConversationLocked(message: ChatMessageData, incrementUnread: Boolean) {
        _conversations.update { list ->
            list.map { conversation ->
                if (conversation.conversationId != message.conversationId) return@map conversation
                conversation.copy(
                    lastMessage = message,
                    updatedAt = maxOf(conversation.updatedAt, message.sentAt),
                    unreadCount = conversation.unreadCount + if (incrementUnread) 1 else 0
                )
            }.sortedByDescending { it.updatedAt }
        }
        if (incrementUnread) _unreadCount.update { it + 1 }
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
