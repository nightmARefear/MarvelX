package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface MarketplaceContract {

    sealed interface Event : BaseEvent {
        data object Init : Event

        data class OnPromoCarouselScrolled(val newIndex: Int) : Event
        data class OnCategoryClicked(val categoryCode: String) : Event
        data class OnListingClicked(val listingId: String) : Event

        sealed interface SearchBarEvents : Event {
            data class OnSearchTermChanged(val searchTerm: String) : SearchBarEvents
            data object OnDeleteIconClicked : SearchBarEvents
        }
    }

    data class State(
        val isLoading: Boolean = true,

        val marketplaceUi: MarketplaceUi? = null,

        val searchBarTerm: String = "",
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToListingsByCategoryScreen(val categoryCode: String) : Navigation
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
        }
    }
}