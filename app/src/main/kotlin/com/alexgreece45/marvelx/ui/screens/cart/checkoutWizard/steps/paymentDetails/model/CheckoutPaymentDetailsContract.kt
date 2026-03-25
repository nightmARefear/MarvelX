package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

sealed interface CheckoutPaymentDetailsContract {

    sealed interface Event : BaseEvent {

        data object Init : Event

        data class OnCardholderNameChanged(val newCardholderName: String) : Event
        data class OnCardNumberChanged(val newCardNumber: String) : Event
        data class OnExpirationDateChanged(val newExpirationDate: String) : Event
        data class OnCvvChanged(val newCvv: String) : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val isInitialized: Boolean = false,

        val cardholderName: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val cardNumber: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val expirationDate: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val cvv: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),

        val expirationDatePlaceholder: String = "",

    ) : BaseState

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
        }

        data class ShowToastMessage(val message: String) : Effect
        data class SetStickyBottomButtonEnabled(val enabled: Boolean) : Effect
    }
}
