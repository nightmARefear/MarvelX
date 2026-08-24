package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.repository.marketplace.model.DateRangeOption
import com.alexgreece45.marvelx.data.repository.marketplace.model.ListingFilter
import com.alexgreece45.marvelx.data.repository.marketplace.model.ListingSort
import com.alexgreece45.marvelx.database.ListingCategoryEntity
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import kotlinx.collections.immutable.ImmutableList

sealed interface MarketplaceContract {

    sealed interface Event : BaseEvent {
        data object Init : Event

        data class OnPromoCarouselScrolled(val newIndex: Int) : Event
        data class OnCategoryClicked(val categoryCode: String) : Event
        data class OnListingClicked(val listingId: String) : Event

        sealed interface SearchBar : Event {
            data object OnClicked : SearchBar
            data class OnSearchTermChanged(val searchTerm: String) : SearchBar
            data object OnDeleteIconClicked : SearchBar
        }

        data object OnErrorButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val hasError: Boolean = false,
        val marketplaceUi: MarketplaceUi? = null,

        val lastPromoSectionScrollMillis: Long = 0L,
        val searchBarTerm: String = "",

        val categories: List<EnumGeneric> = emptyList(),
        val datePostedRanges: List<DateRangeOption>
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToListingsByCategoryScreen(val categoryCode: String) : Navigation
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
        }
    }
}