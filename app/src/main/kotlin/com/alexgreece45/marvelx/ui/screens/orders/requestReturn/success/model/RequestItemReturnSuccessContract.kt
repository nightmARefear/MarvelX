package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.model

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnUi

internal sealed interface RequestItemReturnSuccessContract {
    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data object OnErrorButtonClicked : Event
        data object OnStickyBottomButtonClicked : Event
    }

    data class State(
        val uiState: UiState<OrderItemReturnUi, Unit> = UiState.Loading,

        val orderId: String = "",
        val orderItemId: String = "",
        val orderItemReturnId: OrderItemReturnId = OrderItemReturnId.EMPTY,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class NavigateToRequestItemReturnScreen(
                val orderId: String,
                val orderItemId: String
            ) : Navigation
        }
    }
}