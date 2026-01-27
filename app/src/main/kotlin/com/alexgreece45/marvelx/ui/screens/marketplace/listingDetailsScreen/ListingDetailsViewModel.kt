package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListingDetailsViewModel(
    private val listingInteractor: ListingInteractor,
    private val listingUiMapper: ListingUiMapper,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys,
) :
    BaseViewModel<ListingDetailsContract.Event, ListingDetailsContract.State, ListingDetailsContract.Effect>() {

    override fun setInitialState(): ListingDetailsContract.State = ListingDetailsContract.State()

    override fun handleEvents(event: ListingDetailsContract.Event) {
        when (event) {
            is ListingDetailsContract.Event.Init -> init(event.listingId)
            ListingDetailsContract.Event.OnBack -> onBack()
            ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked -> onFavoriteButtonClicked()
            ListingDetailsContract.Event.ToolbarActionButton.OnShareClicked -> onShareButtonClicked()
            ListingDetailsContract.Event.ToolbarActionButton.OnEditClicked -> onEditButtonClicked()
            is ListingDetailsContract.Event.OnCarouselScrolled -> onCarouselScrolled(event.index)
            ListingDetailsContract.Event.OnSellerInfoClicked -> onSellerInfoClicked()
            ListingDetailsContract.Event.OnAddToBasketButtonClicked -> onAddToBasketButtonClicked()
            ListingDetailsContract.Event.OnErrorRetryButtonClicked -> onErrorRetryButtonClicked()
        }
    }

    /**
     * Event functions.
     */

    private fun init(listingId: String) {
        setState {
            copy(
                isLoading = true,
                listingId = listingId
            )
        }
        getListing()
    }

    private fun onBack() {
        setEffect {
            ListingDetailsContract.Effect.Navigation.GoBack
        }
    }

    private fun onFavoriteButtonClicked() {
        if (currentState.isFavoriteButtonLoading) return

        when (val listing = currentState.listing) {
            null -> {
                return
            }

            else -> {
                setState {
                    copy(
                        isFavoriteButtonLoading = true
                    )
                }
                viewModelScope.launch {
                    if (listing.favorite) {
                        val wasRemoved = listingInteractor.removeFromFavorites(listing.listingId)
                        if (wasRemoved) {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false,
                                    listing = listing.copy(favorite = false)
                                )
                            }
                        } else {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false
                                )
                            }
                            showToastMessage(
                                message = resourceProvider.getString(R.string.screen_listing_details_error_removing_from_favorites)
                            )
                        }
                    } else {
                        val wasAdded = listingInteractor.addToFavorites(listing.listingId)
                        if (wasAdded) {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false,
                                    listing = listing.copy(favorite = true)
                                )
                            }
                        } else {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false
                                )
                            }
                            showToastMessage(
                                message = resourceProvider.getString(R.string.screen_listing_details_error_adding_to_favorites)
                            )
                        }
                    }
                }
            }
        }

    }

    private fun onShareButtonClicked() {
        currentState.listing?.let { listing ->
            setEffect {
                ListingDetailsContract.Effect.ShareListingUrl(
                    url = DeepLinkWebUrl.ListingDetails.webUrl + listing.listingId,
                    title = resourceProvider.getString(
                        R.string.screen_listing_details_share_intent_title,
                        listing.title
                    )
                )
            }
        }
    }

    private fun onEditButtonClicked() {
        currentState.listingId?.let { listingId ->
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToEditListingScreen(listingId)
            }
        }
    }

    private fun onCarouselScrolled(index: Int) {
        setState {
            copy(
                listing = listing?.copy(
                    selectedImageIndex = index
                )
            )
        }
    }

    private fun onSellerInfoClicked() {
        currentState.listing?.sellerUserId?.let { userId ->
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToProfileDetailsScreen(userId)
            }
        }
    }

    private fun onAddToBasketButtonClicked() {

    }

    private fun onErrorRetryButtonClicked() {
        getListing()
    }

    /**
     * Helper functions.
     */


    private fun getListing() {
        val listingId = currentState.listingId ?: return

        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        viewModelScope.launch {
            when (val partialState = listingInteractor.getListing(listingId)) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    showGenericErrorMessage()
                }

                is InteractorPartialState.Success -> {
                    when (val listing =
                        listingUiMapper.toUi(partialState.data, prefKeys.getUserId())) {
                        null -> {
                            showGenericErrorMessage()
                        }

                        else -> {
                            // Get seller info.
                            when (val profileDetailsPartialState =
                                userInteractor.getProfileDetails(listing.sellerUserId).first()) {
                                is GetProfileDetailsPartialState.Error,
                                is GetProfileDetailsPartialState.Failed,
                                GetProfileDetailsPartialState.NoInternetConnection -> {
                                    showGenericErrorMessage()
                                }

                                is GetProfileDetailsPartialState.Success -> {
                                    setState {
                                        copy(
                                            isLoading = false,
                                            listing = listing,
                                            sellerProfileDetails = profileDetailsPartialState.profileDetailsDomain
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showGenericErrorMessage() {
        setState {
            copy(
                isLoading = false,
                hasError = true
            )
        }
    }

    private fun showToastMessage(message: String) {
        setEffect {
            ListingDetailsContract.Effect.ShowToastMessage(message)
        }
    }
}
