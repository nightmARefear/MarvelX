package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.navigation.Screen

sealed interface CheckoutWizardContract {

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

            /**
             * Triggered when the user attempts to navigate to a screen outside the checkout wizard's flow.
             */
            data class ExternalScreen(val externalScreen: Screen) : OnNavigationEvents
        }

        data object OnExitWizard : Event
    }

    /**
     * @param exitWizardAlertDialogPositiveActionScreen the screen to navigate to when the positive
     * button of the alert dialog is clicked. If `null`, then the wizard closes.
     * @param orderId The order ID of the newly created order.
     */
    data class State(
        val toolbarTitle: String = "",
        val wizardStep: CheckoutWizardStep = CheckoutWizardStep.SHIPPING_DETAILS,

        val stickyBottomButtonEnabled: Boolean = false,

        val exitWizardAlertDialogVisible: Boolean = false,
        val exitWizardAlertDialogPositiveActionScreen: Screen? = null,

        val orderId: String = ""
    ) : BaseState {

        val showToolbar: Boolean get() = wizardStep != CheckoutWizardStep.SUCCESS
        val wizardProgress: Float get() = wizardStep.order.toFloat() / CHECKOUT_WIZARD_TOTAL_STEPS.toFloat()
    }

    sealed interface Effect : BaseEffect {
        data class ShowToastMessage(val message: String) : Effect

        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
            data class NavigateToExternalScreen(val externalScreen: Screen) : Navigation
            data class NavigateToOrderDetailsScreen(val orderId: String) : Navigation
        }
    }
}
