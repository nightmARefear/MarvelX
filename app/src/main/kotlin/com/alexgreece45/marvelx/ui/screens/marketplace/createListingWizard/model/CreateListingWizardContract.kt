package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails

sealed interface CreateListingWizardContract {

    sealed interface Event : BaseEvent {
        data class Init(val listingId: String?) : Event

        data object OnBackButtonClicked : Event
        data object OnCloseButtonClicked : Event
        data object OnNextButtonClicked : Event

        data class SetNextButtonEnabled(val enabled: Boolean) : Event

        sealed interface AlertDialogEvents : Event {
            data object OnNegativeButtonClicked : AlertDialogEvents
            data object OnPositiveButtonClicked : AlertDialogEvents
        }
    }

    data class State(
        val isLoading: Boolean = true,
        val toolbarTitle: String = "",

        val wizardMode: CreateListingWizardMode? = null,
        val wizardStep: CreateListingWizardStep = CreateListingWizardStep.BASIC_DETAILS,

        val listingUi: ListingUi? = null,

        val error: CreateListingWizardErrorData? = null,
        val isNextButtonEnabled: Boolean = false,
        val isAlertDialogVisible: Boolean = false,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
        }

        data class InitBasicDetailsScreen(val updateListingBasicDetails: UpdateListingBasicDetails?) :
            Effect

        data class ShowToastMessage(val message: String) : Effect
    }
}
