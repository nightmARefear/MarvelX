package com.alexgreece45.marvelx.ui.screens.chat.inbox.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.model.ConversationUi
import kotlinx.collections.immutable.ImmutableList

internal interface InboxContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnRefresh : Event
        data object OnErrorButtonClicked : Event
        data class OnConversationClicked(val conversationId: String) : Event
    }

    data class State(
        val uiState: UiState<ImmutableList<ConversationUi>, Unit> = UiState.Loading,
        val isRefreshing: Boolean = false
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToConversation(val conversationId: String) : Navigation
        }
    }
}
