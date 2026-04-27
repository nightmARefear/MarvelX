package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn.OrderItemReturnReasonUi

sealed interface RequestItemReturnContract {

    sealed interface Event : BaseEvent {
        data class Init(
            val orderId: String,
            val orderItemId: String,
            val orderItemTitle: String,
            val orderItemCoverImageUrl: String,
            val orderItemPriceLabel: String
        ) : Event

        data object OnBack : Event
        data class OnReturnReasonSelected(val reason: OrderItemReturnReasonUi) : Event
        data class OnDescriptionChanged(val description: String) : Event
        data object OnSubmitClicked : Event
    }

    data class State(
        val orderId: String = "",
        val orderItemId: String = "",
        val orderItemTitle: String = "",
        val orderItemCoverImageUrl: String = "",
        val orderItemPriceLabel: String = "",
        val selectedReason: OrderItemReturnReasonUi? = null,
        val description: String = "",

        val isSubmitting: Boolean = false,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
        }
    }
}
