package com.alexgreece45.marvelx.ui.screens.orders.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import kotlinx.collections.immutable.ImmutableList

sealed interface OrdersContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event

        sealed interface OrderEvents : Event {
            data class OnClicked(val orderId: String) : OrderEvents
            data class OnStatusChipClicked(
                val orderId: String,
                val orderStatusUi: OrderStatusUi
            ) : OrderEvents
        }

        sealed interface OrderStatusInfoBottomSheetEvents : Event {
            data object OnBottomSheetDismissed : OrderStatusInfoBottomSheetEvents
            data class OnViewOrderDetailsClicked(val orderId: String) :
                OrderStatusInfoBottomSheetEvents
        }

        data object OnEmptyOrdersButtonClicked : Event
        data object OnErrorButtonClicked : Event
    }

    data class State(
        val uiState: UiState<ImmutableList<OrderPreviewUi>, Unit> = UiState.Loading,

        val ordersBottomSheetType: OrdersBottomSheetType? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        data object HideBottomSheet : Effect
        data object ShowBottomSheet : Effect

        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToOrderDetailsScreen(val orderId: String) : Navigation
            data object GoToMarketplaceScreen : Navigation
        }
    }
}