package com.alexgreece45.marvelx.ui.screens.myListings.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

sealed interface MyListingsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnFabButtonClicked : Event
        data object OnEndOfListScrolled : Event

        sealed interface ListingEvents : Event {
            data class OnClicked(val listingId: String) : ListingEvents
            data class OnMoreActionsButtonClicked(val listing: ListingUi) : ListingEvents
        }

        sealed interface BottomSheetEvents : Event {
            data object OnEditListingClicked : BottomSheetEvents
            data object OnDeleteListingClicked : BottomSheetEvents
        }
    }

    data class State(
        val isLoading: Boolean = true,
        val isFetchingNewItems: Boolean = false,

        val currentPage: Int = 1,
        val isScrollingInitialized: Boolean = false,

        val allListings: PersistentList<ListingUi> = persistentListOf(),

        val selectedListingData: MyListingsSelectedListingData? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
            data object GoToCreateListingScreen : Navigation
            data class GoToEditListingScreen(val listingId: String) : Navigation
        }

        sealed interface BottomSheetEffects : Effect {
            data object Hide : BottomSheetEffects
            data object Show : BottomSheetEffects
        }

        data class ShowToastMessage(val message: String) : Effect
    }
}
