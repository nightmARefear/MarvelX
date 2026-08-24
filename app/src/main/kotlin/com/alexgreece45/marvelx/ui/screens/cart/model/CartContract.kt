package com.alexgreece45.marvelx.ui.screens.cart.model

import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCheckoutInfoUi

sealed interface CartContract {

    sealed interface Event : BaseEvent {

        data class Init(val orderCheckoutInfo: OrderCheckoutInfoDomain?) : Event
        data object OnEmptyCartViewButtonClicked : Event
        sealed interface CartItemEvents : Event {
            data class OnClicked(val listingId: String) : CartItemEvents
            data class OnFavoriteButtonClicked(val listingId: String) : CartItemEvents
            data class OnDeleteButtonClicked(val listingId: String) : CartItemEvents
        }

        sealed interface CartCheckoutEvents : Event {
            data object OnEditShippingDetailsButtonClicked : CartCheckoutEvents
            data object OnEditPaymentDetailsButtonClicked : CartCheckoutEvents
        }

        data object OnErrorButtonClicked : Event
        data object OnCartSummaryErrorButtonClicked : Event

        data object OnCartSummaryButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val isSummaryLoading: Boolean = false,
        val isCheckingOut: Boolean = false,

        val cartInfo: CartInfoUi? = null,
        val orderCheckoutInfo: OrderCheckoutInfoUi? = null,

        val error: Boolean = false,
        val cartSummaryError: Boolean = false,
    ) : BaseState {

        val isCheckout: Boolean get() = orderCheckoutInfo != null
    }

    sealed interface Effect : BaseEffect {

        data class ShowToastMessage(val message: String) : Effect

        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data object GoToMarketplaceScreen : Navigation
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
            data object GoToCheckoutScreen : Navigation
            data object GoToEditCheckoutShippingDetailsScreen : Navigation
            data object GoToEditCheckoutPaymentDetailsScreen : Navigation
            data class GoToCheckoutSuccessScreen(val orderId: String) : Navigation
        }
    }
}