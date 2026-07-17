package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model

import com.alexgreece45.marvelx.core.data.model.listing.ListingId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface CreateListingSuccessContract {

    sealed interface Event : BaseEvent {
        data class Init(val listingId: String, val wizardMode: String) : Event
        data object OnViewListingClicked : Event
        data object OnBackClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val successMessage: String = "",
        val listingId: ListingId = ListingId.EMPTY
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class NavigateToListingDetailsScreen(val listingId: ListingId) : Navigation
        }
    }
}