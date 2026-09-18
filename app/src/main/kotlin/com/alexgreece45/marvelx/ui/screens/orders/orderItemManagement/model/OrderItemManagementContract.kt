package com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.model

import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStatusUi

internal interface OrderItemManagementContract {

    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data object OnErrorButtonClicked : Event

        data object OnMarkAsShippedClicked : Event

        data object OnReturnCardClicked : Event
    }

    data class State(
        val uiState: UiState<OrderItemUi, Unit> = UiState.Loading,

        val orderId: OrderId = OrderId.EMPTY,
        val orderItemId: OrderId = OrderId.EMPTY,

        // Set only when a return request already exists for this order item (any status). Null
        // means either it's still loading or there is no return to display.
        val existingReturnStatus: OrderItemReturnStatusUi? = null,
    ) : BaseState {
        /**
         * Dispatch is the only transition the seller owns; everything past it is reported by the
         * courier, so the action disappears as soon as the item leaves the seller.
         */
        val isMarkAsShippedAvailable: Boolean
            get() = (uiState as? UiState.Success)?.data?.status == OrderItemStatusUi.Processing
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToRequestItemReturnScreen(
                val orderId: String,
                val orderItemId: String
            ) : Navigation
        }

        data class ShowToast(val message: String) : Effect
    }
}