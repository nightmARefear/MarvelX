package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model

import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemTrackingHistoryUi
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderItemDetails

internal interface OrderItemTrackingHistoryContract {

    sealed interface Event : BaseEvent {
        data class Init(
            val orderId: String,
            val orderItemId: String,
            val orderItemTitle: String,
            val orderItemCoverImageUrl: String,
            val orderItemPriceLabel: String,
            val sellerId: String
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

        data object OnReturnActionButtonClicked : Event
        data object OnRefundActionButtonClicked : Event

        sealed interface ExtraActionButtonEvents : Event {
            data object OnContactSupportClicked : ExtraActionButtonEvents
            data object OnContactSellerClicked : ExtraActionButtonEvents
        }
    }

    data class State(
        val uiState: UiState<OrderItemTrackingHistoryUi, OrderItemTrackingHistoryErrorType> = UiState.Loading,
        val sellerDetailsUiState: UiState<RepositoryBasicProfileDetails, Unit> = UiState.Loading,

        val orderItemDetails: OrderItemDetails = OrderItemDetails.EMPTY,
        val sellerUserId: UserId = UserId.EMPTY,

        val orderItemTrackingHistoryUi: OrderItemTrackingHistoryUi? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToProfileDetailsScreen(val userId: String) : Navigation
            data class GoToRequestItemReturnScreen(
                val orderId: String,
                val orderItemId: String,
                val orderItemTitle: String,
                val orderItemCoverImageUrl: String,
                val orderItemPriceLabel: String
            ) : Navigation

            data class GoToRequestItemRefundScreen(val orderItemDetails: OrderItemDetails) :
                Navigation

            data class GoToContactSupportScreen(val orderItemDetails: OrderItemDetails) : Navigation
        }

        data class SetClipboardText(val toastMessage: String, val text: String) : Effect
    }
}