package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model

import com.alexgreece45.marvelx.core.data.model.listing.ListingId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode

sealed interface CreateListingSuccessContract {

    sealed interface Event : BaseEvent {
        data object OnViewListingClicked : Event
        data object OnBackClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val listingId: ListingId = ListingId.EMPTY,
        val wizardMode: CreateListingWizardMode? = null,

        val successMessage: String = ""
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class NavigateToListingDetailsScreenAndPopUpToSuccess(val listingId: ListingId) :
                Navigation

            data class NavigateAndPopUpToListingDetailsScreen(val listingId: ListingId) :
                Navigation
        }
    }
}