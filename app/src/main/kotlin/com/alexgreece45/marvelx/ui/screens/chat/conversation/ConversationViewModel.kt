package com.alexgreece45.marvelx.ui.screens.chat.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.data.model.chat.ChatMessageData
import com.alexgreece45.marvelx.data.model.chat.ConversationData
import com.alexgreece45.marvelx.data.repository.chat.ChatRepository
import com.alexgreece45.marvelx.data.repository.chat.OpenConversationResult
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.conversation.model.ConversationContract
import com.alexgreece45.marvelx.ui.screens.chat.conversation.model.ConversationErrorType
import com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation.ConversationRoute
import com.alexgreece45.marvelx.ui.screens.chat.model.toConversationListItems
import com.alexgreece45.marvelx.ui.screens.chat.model.toUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class ConversationViewModel(
    private val chatRepository: ChatRepository,
    private val userSessionRepository: UserSessionRepository,
    private val configPriceLogic: ConfigPriceLogic,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ConversationContract.Event, ConversationContract.State, ConversationContract.Effect>() {

    private val route = savedStateHandle.toRoute<ConversationRoute>()
    private var conversationId: String? = null
    private var currentUserId: String = ""
    private var observeMessagesJob: Job? = null

    init {
        open()
    }

    override fun setInitialState(): ConversationContract.State = ConversationContract.State()

    override fun handleEvents(event: ConversationContract.Event) {
        when (event) {
            ConversationContract.Event.OnBack -> setEffect { ConversationContract.Effect.Navigation.GoBack }
            ConversationContract.Event.OnErrorButtonClicked -> open()

            is ConversationContract.Event.OnMessageTextChanged -> {
                setState {
                    copy(messageField = messageField.copy(value = event.text.take(messageMaxLength)))
                }
            }

            ConversationContract.Event.OnSendClicked -> send()
            is ConversationContract.Event.OnRetryMessageClicked -> retry(event.clientMessageId)
            ConversationContract.Event.OnLoadOlderMessages -> loadOlderMessages()

            ConversationContract.Event.OnParticipantClicked -> {
                currentState.conversation?.let {
                    setEffect { ConversationContract.Effect.Navigation.GoToProfileDetails(it.participant.userId) }
                }
            }

            ConversationContract.Event.OnPinnedOrderItemClicked -> onPinnedOrderItemClicked()
        }
    }

    override fun onCleared() {
        chatRepository.setOpenConversation(null)
        super.onCleared()
    }

    // Opening.

    private fun open() {
        setState { copy(uiState = UiState.Loading) }

        viewModelScope.launch {
            currentUserId = userSessionRepository.getUserId()

            val result = when {
                route.conversationId != null -> chatRepository.getConversation(route.conversationId).toOpenResult()
                route.userId != null -> chatRepository.openDirectConversation(route.userId)
                route.orderId != null && route.orderItemId != null ->
                    chatRepository.openOrderItemConversation(route.orderId, route.orderItemId)

                else -> OpenConversationResult.Failed("No conversation target was provided.")
            }

            when (result) {
                is OpenConversationResult.Success -> onOpened(result.conversation)
                OpenConversationResult.Forbidden -> setState { copy(uiState = UiState.Error(ConversationErrorType.Forbidden)) }
                OpenConversationResult.NetworkError -> setState { copy(uiState = UiState.Error(ConversationErrorType.NoInternet)) }
                is OpenConversationResult.Failed -> setState { copy(uiState = UiState.Error(ConversationErrorType.Generic)) }
            }
        }
    }

    private suspend fun onOpened(conversation: ConversationData) {
        val id = conversation.conversationId
        conversationId = id
        chatRepository.setOpenConversation(id)

        setState {
            copy(
                uiState = UiState.Success(conversation.toUi(configPriceLogic.priceScale)),
                isCurrentUserSeller = conversation.orderItem?.sellerUserId == currentUserId,
                hasMoreHistory = true
            )
        }

        observeMessages(id)

        val loaded = chatRepository.loadLatestMessages(id)
        if (loaded is RepositoryResult.Success && loaded.data.size < PAGE_SIZE) {
            setState { copy(hasMoreHistory = false) }
        }
        chatRepository.markConversationRead(id)
        setEffect { ConversationContract.Effect.ScrollToLatest }
    }

    private fun observeMessages(id: String) {
        observeMessagesJob?.cancel()
        observeMessagesJob = viewModelScope.launch {
            chatRepository.messagesByConversation
                .map { it[id].orEmpty() }
                .collect { messages -> render(messages) }
        }
    }

    private fun render(messages: List<ChatMessageData>) {
        setState {
            copy(listItems = messages.map { it.toUi(currentUserId) }.toConversationListItems())
        }
    }

    // Sending.

    private fun send() {
        val id = conversationId ?: return
        val text = currentState.messageField.value
        if (text.isBlank()) return

        setState { copy(messageField = messageField.copy(value = "")) }
        viewModelScope.launch {
            chatRepository.sendMessage(conversationId = id, body = text)
        }
        setEffect { ConversationContract.Effect.ScrollToLatest }
    }

    private fun retry(clientMessageId: String) {
        val id = conversationId ?: return
        viewModelScope.launch {
            chatRepository.retryMessage(conversationId = id, clientMessageId = clientMessageId)
        }
    }

    // History.

    private fun loadOlderMessages() {
        val id = conversationId ?: return
        if (currentState.isLoadingOlderMessages || !currentState.hasMoreHistory) return

        // Optimistic sends have no server id yet, so the cursor is the oldest confirmed message.
        val oldestServerMessageId = chatRepository.messagesByConversation.value[id]
            ?.firstOrNull { it.messageId.isNotBlank() }
            ?.messageId
            ?: return

        setState { copy(isLoadingOlderMessages = true) }
        viewModelScope.launch {
            val result = chatRepository.loadOlderMessages(id, beforeMessageId = oldestServerMessageId)
            setState {
                copy(
                    isLoadingOlderMessages = false,
                    hasMoreHistory = when (result) {
                        is RepositoryResult.Success -> result.data.size >= PAGE_SIZE
                        else -> hasMoreHistory
                    }
                )
            }
        }
    }

    // Pinned item.

    private fun onPinnedOrderItemClicked() {
        val conversation = currentState.conversation ?: return
        val orderItem = conversation.orderItem ?: return

        setEffect {
            if (currentState.isCurrentUserSeller) {
                ConversationContract.Effect.Navigation.GoToOrderItemManagement(
                    orderId = orderItem.orderId,
                    orderItemId = orderItem.orderItemId
                )
            } else {
                ConversationContract.Effect.Navigation.GoToOrderItemTrackingHistory(
                    orderId = orderItem.orderId,
                    orderItemId = orderItem.orderItemId,
                    orderItemTitle = orderItem.title,
                    orderItemCoverImageUrl = orderItem.coverImageUrl,
                    orderItemPriceLabel = orderItem.priceLabel,
                    sellerUserId = orderItem.sellerUserId
                )
            }
        }
    }

    private fun RepositoryResult<ConversationData>.toOpenResult(): OpenConversationResult =
        when (this) {
            is RepositoryResult.Success -> OpenConversationResult.Success(data)
            is RepositoryResult.Failed -> OpenConversationResult.Failed(failureMessage)
            RepositoryResult.NetworkError -> OpenConversationResult.NetworkError
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
