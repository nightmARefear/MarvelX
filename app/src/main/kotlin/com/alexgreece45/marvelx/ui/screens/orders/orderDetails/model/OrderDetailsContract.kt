package com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model

import com.alexgreece45.marvelx.core.data.model.order.OrderId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi

internal sealed interface OrderDetailsContract {
    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data object OnErrorButtonClicked : Event

        data object OnOrderStatusBannerClicked : Event

        sealed interface OrderItemEvents : Event {
            data class OnTrackButtonClicked(val orderItem: OrderItemUi) : OrderItemEvents
        }
    }

    data class State(
        val uiState: UiState<OrderUi, Unit> = UiState.Loading,

        val orderId: OrderId = OrderId.EMPTY,

        val bottomSheetType: OrderDetailsBottomSheetType? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {

            data object GoBack : Navigation
            data class NavigateToOrderItemTrackingHistoryScreen(
                val orderId: String,
                val orderItemId: String,
                val orderItemTitle: String,
                val orderItemCoverImageUrl: String,
                val orderItemPriceLabel: String,
                val sellerUserId: String
            ) : Navigation
        }

        data object ShowBottomSheet : Effect
        data object HideBottomSheet : Effect
    }
}