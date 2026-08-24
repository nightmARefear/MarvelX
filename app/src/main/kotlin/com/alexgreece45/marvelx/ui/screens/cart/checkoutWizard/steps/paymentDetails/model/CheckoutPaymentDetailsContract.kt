package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui.screens.orders.model.CardBrandUi
import com.alexgreece45.marvelx.util.FormValidationResult

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

        // TODO: Replace with InputFieldUi.
        val cardholderNameField: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val cardNumberField: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val expirationDateField: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val cvvField: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),

        val expirationDatePlaceholder: String = "",
    ) : BaseState {

        val cardBrand: CardBrandUi get() = CardBrandUi.getCardBrandUi(cardNumberField.value)
    }

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
        }

        data class ShowToastMessage(val message: String) : Effect
        data class SetStickyBottomButtonEnabled(val enabled: Boolean) : Effect
    }
}