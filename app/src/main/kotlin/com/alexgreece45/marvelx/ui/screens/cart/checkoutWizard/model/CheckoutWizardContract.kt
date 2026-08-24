package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

internal sealed interface CheckoutWizardContract {

    sealed interface Event : BaseEvent {

        data object Init : Event

        data object OnBackButtonClicked : Event
        data object OnCloseButtonClicked : Event

        data object OnStickyBottomButtonClicked : Event

        sealed interface AlertDialogEvents : Event {
            data object OnNegativeButtonClicked : AlertDialogEvents
            data object OnPositiveButtonClicked : AlertDialogEvents
        }

        data class SetStickyBottomButtonEnabled(val enabled: Boolean) : Event
        data class OnShowToastMessage(val message: String) : Event
        data object OnActionNotSupported : Event

        sealed interface OnNavigationEvents : Event {
            data object EditShippingDetailsScreen : OnNavigationEvents
            data object EditPaymentDetailsScreen : OnNavigationEvents
            data class OrderSuccessScreen(val orderId: String) : OnNavigationEvents
            data class OrderDetailsScreen(val orderId: String) : OnNavigationEvents
            data object OnNavigateToMarketplaceScreen : OnNavigationEvents
        }

        data object OnExitWizard : Event
    }

    /**
     * @param orderId The order ID of the newly created order.
     */
    data class State(
        val toolbarTitle: String = "",
        val wizardStep: CheckoutWizardStep = CheckoutWizardStep.SHIPPING_DETAILS,

        val stickyBottomButtonEnabled: Boolean = false,

        val alertDialogAction: CheckoutWizardExitAlertDialogAction? = null,

        val orderId: String = ""
    ) : BaseState {

        val wizardProgress: Float get() = (wizardStep.ordinal + 1) / CheckoutWizardStep.entries.size.toFloat()
    }

    sealed interface Effect : BaseEffect {
        data class ShowToastMessage(val message: String) : Effect

        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
            data object NavigateToMarketplaceScreen : Navigation
            data class NavigateToOrderDetailsScreen(val orderId: String) : Navigation
            data class NavigateToCheckoutSuccessScreen(val orderId: String) : Navigation
        }
    }
}