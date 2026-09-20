package com.alexgreece45.marvelx.ui.screens.chat.conversation.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.chat.model.ConversationListItemUi
import com.alexgreece45.marvelx.ui.screens.chat.model.ConversationUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal enum class ConversationErrorType {
    Generic,
    NoInternet,

    /** The server refused to open the chat — one user has blocked the other. */
    Forbidden
}

internal interface ConversationContract {

    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data object OnErrorButtonClicked : Event

        data class OnMessageTextChanged(val text: String) : Event
        data object OnSendClicked : Event
        data class OnRetryMessageClicked(val clientMessageId: String) : Event

        data object OnLoadOlderMessages : Event

        data object OnParticipantClicked : Event
        data object OnPinnedOrderItemClicked : Event
    }

    data class State(
        val uiState: UiState<ConversationUi, ConversationErrorType> = UiState.Loading,

        /** Chronological, oldest first; the screen renders it reversed. */
        val listItems: ImmutableList<ConversationListItemUi> = persistentListOf(),

        val messageField: InputFieldUi.Text = InputFieldUi.Text(isMandatory = false),
        val messageMaxLength: Int = MESSAGE_MAX_LENGTH,

        val isLoadingOlderMessages: Boolean = false,
        val hasMoreHistory: Boolean = true,

        /** Whether the current user is the seller of the pinned order item (if any). */
        val isCurrentUserSeller: Boolean = false
    ) : BaseState {
        val isSendEnabled: Boolean
            get() = uiState is UiState.Success && messageField.value.isNotBlank()

        val conversation: ConversationUi?
            get() = (uiState as? UiState.Success)?.data

        companion object {
            const val MESSAGE_MAX_LENGTH = 2000
        }
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToProfileDetails(val userId: String) : Navigation
            data class GoToOrderItemTrackingHistory(
                val orderId: String,
                val orderItemId: String,
                val orderItemTitle: String,
                val orderItemCoverImageUrl: String,
                val orderItemPriceLabel: String,
                val sellerUserId: String
            ) : Navigation

            data class GoToOrderItemManagement(
                val orderId: String,
                val orderItemId: String
            ) : Navigation
        }

        data object ScrollToLatest : Effect
    }
}
