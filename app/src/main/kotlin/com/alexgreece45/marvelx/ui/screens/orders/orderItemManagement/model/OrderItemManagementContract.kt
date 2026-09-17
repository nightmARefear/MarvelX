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

        sealed interface DropdownEvents : Event {
            data object OnClicked : DropdownEvents
            data object OnDismissed : DropdownEvents
            data class OnItemSelected(val status: OrderItemStatusUi) : DropdownEvents
        }

        data object OnApplyStatusButtonClicked : Event

        data object OnReturnCardClicked : Event
    }

    data class State(
        val uiState: UiState<OrderItemUi, Unit> = UiState.Loading,

        val orderId: OrderId = OrderId.EMPTY,
        val orderItemId: OrderId = OrderId.EMPTY,

        val isDropdownExpanded: Boolean = false,
        val selectedNewStatus: OrderItemStatusUi? = null,

        // Set only when a return request already exists for this order item (any status). Null
        // means either it's still loading or there is no return to display.
        val existingReturnStatus: OrderItemReturnStatusUi? = null,
    ) : BaseState

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