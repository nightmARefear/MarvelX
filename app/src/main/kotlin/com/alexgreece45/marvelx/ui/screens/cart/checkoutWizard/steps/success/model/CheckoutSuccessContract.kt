package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface CheckoutSuccessContract {

    sealed interface Event : BaseEvent {
        data class Init(val orderId: String) : Event

        data object OnViewOrderDetailsButtonClicked : Event
        data object OnContinueShoppingButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val orderId: String = ""
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class NavigateToOrderDetailsScreen(val orderId: String) : Navigation
            data object ExitWizard : Navigation
        }
    }
}
