package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

sealed interface CheckoutShippingDetailsContract {

    sealed interface Event : BaseEvent {

        data object Init : Event

        data class OnFirstNameChanged(val newFirstName: String) : Event
        data class OnLastNameChanged(val newLastName: String) : Event
        data class OnStreetAddressChanged(val newStreetAddress: String) : Event
        data class OnCityChanged(val newCity: String) : Event
        data class OnZipCodeChanged(val newZipCode: String) : Event

        sealed interface FloorNumber : Event {
            data object OnDismissed : FloorNumber
            data object OnClicked : FloorNumber
            data class OnItemSelected(val floorNumber: EnumGeneric) : FloorNumber
        }

        sealed interface LocationType : Event {
            data object OnDismissed : LocationType
            data object OnClicked : LocationType
            data class OnItemSelected(val locationType: EnumGeneric) : LocationType
        }
    }

    data class State(
        val isLoading: Boolean = true,
        val isInitialized: Boolean = false,

        // Input fields.
        val firstName: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val lastName: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val streetAddress: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val city: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val zipCode: WizardInputFieldUi.Integer = WizardInputFieldUi.Integer(),
        val floorNumber: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val locationType: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),

        // Location values.
        val floorNumbers: List<EnumGeneric> = emptyList(),
        val locationTypes: List<EnumGeneric> = emptyList(),
    ) : BaseState

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
        }

        data class ShowToastMessage(val message: String) : Effect
        data class SetStickyBottomButtonEnabled(val enabled: Boolean) : Effect
    }
}
