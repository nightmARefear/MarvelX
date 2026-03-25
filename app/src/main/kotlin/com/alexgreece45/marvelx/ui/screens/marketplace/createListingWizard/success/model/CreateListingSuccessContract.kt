package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface CreateListingSuccessContract {

    sealed interface Event : BaseEvent {
        data object Init : Event

        data object OnViewListingClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val successMessage: String = "",
        val listingId: String = ""
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class NavigateToListingDetailsScreen(val listingId: String) : Navigation
        }
    }
}
