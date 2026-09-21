package com.alexgreece45.marvelx.ui.screens.chat.inbox

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.chat.ChatRepository
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.inbox.model.InboxContract
import com.alexgreece45.marvelx.ui.screens.chat.model.toUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class InboxViewModel(
    private val chatRepository: ChatRepository,
    private val configPriceLogic: ConfigPriceLogic
) : BaseViewModel<InboxContract.Event, InboxContract.State, InboxContract.Effect>() {

    private var observeJob: Job? = null

    override fun setInitialState(): InboxContract.State = InboxContract.State()

    override fun handleEvents(event: InboxContract.Event) {
        when (event) {
            InboxContract.Event.Init -> init()
            InboxContract.Event.OnRefresh -> refresh(isPullToRefresh = true)
            InboxContract.Event.OnErrorButtonClicked -> refresh(isPullToRefresh = false)
            is InboxContract.Event.OnConversationClicked -> {
                setEffect { InboxContract.Effect.Navigation.GoToConversation(event.conversationId) }
            }
        }
    }

    private fun init() {
        if (observeJob != null) return
        refresh(isPullToRefresh = false)
    }

    private fun refresh(isPullToRefresh: Boolean) {
        setState {
            copy(
                uiState = if (isPullToRefresh) uiState else UiState.Loading,
                isRefreshing = isPullToRefresh
            )
        }

        viewModelScope.launch {
            when (chatRepository.refreshConversations()) {
                is RepositoryResult.Success -> {
                    setState { copy(isRefreshing = false) }
                    observeConversations()
                }

                is RepositoryResult.Failed,
                RepositoryResult.NetworkError -> {
                    setState {
                        copy(
                            uiState = if (uiState is UiState.Success) uiState else UiState.Error(Unit),
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Once the first load has landed, the list simply mirrors the repository, which folds in
     * websocket pushes on its own.
     */
    private fun observeConversations() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            chatRepository.conversations.collect { conversations ->
                setState {
                    copy(
                        uiState = UiState.Success(
                            conversations.map { it.toUi(configPriceLogic.priceScale) }.toImmutableList()
                        )
                    )
                }
            }
        }
    }
}
