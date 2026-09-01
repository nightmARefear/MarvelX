package com.alexgreece45.marvelx.ui.screens.myListings.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.util.extensions.model.ShareContentUrlData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

sealed interface MyListingsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnFabButtonClicked : Event
        data object OnEndOfListScrolled : Event

        data object OnCreateListingButtonClicked : Event

        sealed interface ListingEvents : Event {
            data class OnClicked(val listingId: String) : ListingEvents
            data class OnMoreActionsButtonClicked(val listing: MyListingUi) : ListingEvents
        }

        sealed interface BottomSheet : Event {
            data object OnEditListingClicked : BottomSheet
            data object OnShareListingClicked : BottomSheet
            data object OnDeleteListingClicked : BottomSheet
        }

        data object OnDeleteListingDialogNegativeButtonClicked : Event
        data object OnDeleteListingDialogPositiveButtonClicked : Event

        data object OnErrorButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val isFetchingNewItems: Boolean = false,

        val currentPage: Int = 1,

        val allListings: PersistentList<MyListingUi> = persistentListOf(),

        val selectedListing: MyListingUi? = null,

        val error: Boolean = false,
        val deleteListingDialogData: DeleteListingDialogData? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
            data object GoToCreateListingScreen : Navigation
            data class GoToEditListingScreen(val listingId: String) : Navigation
        }

        data object HideBottomSheet : Effect
        data object ShowBottomSheet : Effect

        data class ShareListing(val shareContentUrlData: ShareContentUrlData) : Effect
    }
}