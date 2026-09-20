package com.alexgreece45.marvelx.ui.screens.favoriteListings.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FavoriteListingUi(
    val listingId: String,
    val title: String,
    val priceLabel: String,
    val coverImageUrl: String,
    val favorite: ToggleStateUi,
    val cart: ToggleStateUi
)

fun ListingPreviewDomain.toFavoriteListingUi(priceLabel: String): FavoriteListingUi =
    FavoriteListingUi(
        listingId = listingId,
        title = title,
        priceLabel = priceLabel,
        coverImageUrl = coverImageUrl,
        favorite = ToggleStateUi(isChecked = favorite),
        cart = ToggleStateUi(isChecked = cart)
    )

sealed interface FavoriteListingsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnRetryClicked : Event

        data class OnListingClicked(val listingId: String) : Event
        data class OnFavoriteClicked(val listingId: String) : Event
        data class OnCartClicked(val listingId: String) : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val hasError: Boolean = false,

        val listings: ImmutableList<FavoriteListingUi> = persistentListOf()
    ) : BaseState {
        val isEmpty: Boolean get() = !isLoading && !hasError && listings.isEmpty()
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
        }
    }
}
