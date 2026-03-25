package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model

import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.domain.model_api.order.OrderId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemTrackingHistoryUi

interface OrderItemTrackingHistoryContract {

    sealed interface Event : BaseEvent {
        data class Init(
            val orderId: String,
            val orderItemId: String,
            val sellerId: String,
            val orderItemTitle: String,
            val orderItemCoverImageUrl: String,
            val orderItemPriceLabel: String
        ) : Event

        data object OnBack : Event

        sealed interface CourierCardEvents : Event {
            data object OnCopyButtonClicked : CourierCardEvents
            data object OnViewOnCourierWebsiteButtonClicked : CourierCardEvents
        }

        sealed interface SellerDetailsCardEvents : Event {
            data object OnClicked : SellerDetailsCardEvents
            data object OnErrorButtonClicked : SellerDetailsCardEvents
        }

        data object OnErrorButtonClicked : Event
    }

    data class State(
        val uiState: UiState<OrderItemTrackingHistoryUi, OrderItemTrackingHistoryErrorType> = UiState.Loading,
        val sellerDetailsUiState: UiState<RepositoryBasicProfileDetails, Unit> = UiState.Loading,

        val orderId: OrderId = OrderId(""),
        val orderItemId: OrderId = OrderId(""),
        val sellerId: String = "",
        val orderItemTitle: String = "",
        val orderItemCoverImageUrl: String = "",
        val orderItemPriceLabel: String = "",

        val orderItemTrackingHistoryUi: OrderItemTrackingHistoryUi? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToProfileDetailsScreen(val userId: String) : Navigation
        }

        data class SetClipboardText(val toastMessage: String, val text: String) : Effect
    }
}
