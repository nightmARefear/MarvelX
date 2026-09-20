package com.alexgreece45.marvelx.ui.screens.favoriteListings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.data.repository.cart.CartRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.FavoriteListingUi
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.FavoriteListingsContract
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.toFavoriteListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [FavoriteListingsViewModel::class])
internal class FavoriteListingsViewModel(
    private val listingInteractor: ListingInteractor,
    private val cartRepository: CartRepository,
    private val configPriceLogic: ConfigPriceLogic,
    private val resourceProvider: ResourceProvider,
    snackbarDelegate: SnackbarDelegate
) : BaseViewModel<FavoriteListingsContract.Event, FavoriteListingsContract.State, FavoriteListingsContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    override fun setInitialState(): FavoriteListingsContract.State =
        FavoriteListingsContract.State()

    override fun handleEvents(event: FavoriteListingsContract.Event) {
        when (event) {
            FavoriteListingsContract.Event.Init,
            FavoriteListingsContract.Event.OnRetryClicked -> getFavoriteListings()

            is FavoriteListingsContract.Event.OnListingClicked -> {
                setEffect {
                    FavoriteListingsContract.Effect.Navigation.GoToListingDetailsScreen(event.listingId)
                }
            }

            is FavoriteListingsContract.Event.OnFavoriteClicked -> onFavoriteClicked(event.listingId)
            is FavoriteListingsContract.Event.OnCartClicked -> onCartClicked(event.listingId)
        }
    }

    private fun getFavoriteListings() {
        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }

        viewModelScope.launch {
            when (val partialState = listingInteractor.getFavoriteListings()) {
                is InteractorPartialState.Success -> {
                    val listings = partialState.data.map { listingPreview ->
                        listingPreview.toFavoriteListingUi(
                            priceLabel = listingPreview.price.formatPrice(configPriceLogic.priceScale)
                        )
                    }

                    setState {
                        copy(
                            isLoading = false,
                            listings = listings.toImmutableList()
                        )
                    }
                }

                InteractorPartialState.NoInternetConnection,
                is InteractorPartialState.Failed,
                is InteractorPartialState.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            hasError = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Un-favouriting from this screen removes the listing from the list entirely, since the list
     * is by definition the set of favourited listings. The row is only dropped once the call has
     * succeeded, so a failure leaves the list untouched.
     */
    private fun onFavoriteClicked(listingId: String) {
        val listing = currentState.listings.firstOrNull { it.listingId == listingId } ?: return
        if (listing.favorite.isLoading) return

        setListingState(listingId) { copy(favorite = favorite.copy(isLoading = true)) }

        viewModelScope.launch {
            val wasToggled = if (listing.favorite.isChecked) {
                listingInteractor.removeFromFavorites(listingId)
            } else {
                listingInteractor.addToFavorites(listingId)
            }

            if (!wasToggled) {
                setListingState(listingId) { copy(favorite = favorite.copy(isLoading = false)) }
                showErroneousSnackbar(
                    errorMessage = resourceProvider.getString(R.string.screen_favorite_listings_error_favorite_toggle)
                )
                return@launch
            }

            if (listing.favorite.isChecked) {
                setState {
                    copy(listings = listings.filterNot { it.listingId == listingId }.toImmutableList())
                }
            } else {
                setListingState(listingId) {
                    copy(favorite = ToggleStateUi(isChecked = true, isLoading = false))
                }
            }
        }
    }

    private fun onCartClicked(listingId: String) {
        val listing = currentState.listings.firstOrNull { it.listingId == listingId } ?: return
        if (listing.cart.isLoading) return

        setListingState(listingId) { copy(cart = cart.copy(isLoading = true)) }

        viewModelScope.launch {
            val wasToggled = if (listing.cart.isChecked) {
                cartRepository.deleteCartItem(listingId)
            } else {
                cartRepository.addCartItem(listingId)
            }

            setListingState(listingId) {
                copy(
                    cart = ToggleStateUi(
                        isChecked = if (wasToggled) !listing.cart.isChecked else listing.cart.isChecked,
                        isLoading = false
                    )
                )
            }

            if (!wasToggled) {
                showErroneousSnackbar(
                    errorMessage = resourceProvider.getString(R.string.screen_favorite_listings_error_cart_toggle)
                )
            }
        }
    }

    private fun setListingState(
        listingId: String,
        transform: FavoriteListingUi.() -> FavoriteListingUi
    ) {
        setState {
            copy(
                listings = listings
                    .map { if (it.listingId == listingId) it.transform() else it }
                    .toImmutableList()
            )
        }
    }
}
