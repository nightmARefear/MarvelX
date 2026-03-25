package com.alexgreece45.marvelx.ui.screens.marketplace

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.MarketplaceUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val marketplaceInteractor: MarketplaceInteractor,
    private val marketplaceUiMapper: MarketplaceUiMapper,
) : BaseViewModel<MarketplaceContract.Event, MarketplaceContract.State, MarketplaceContract.Effect>() {

    override fun setInitialState(): MarketplaceContract.State = MarketplaceContract.State()

    override fun handleEvents(event: MarketplaceContract.Event) {
        when (event) {
            MarketplaceContract.Event.Init -> init()
            is MarketplaceContract.Event.OnPromoCarouselScrolled -> onPromoCarouselScrolled(event.newIndex)
            is MarketplaceContract.Event.OnCategoryClicked -> onCategoryClicked(event.categoryCode)
            is MarketplaceContract.Event.OnListingClicked -> onListingClicked(event.listingId)
            is MarketplaceContract.Event.SearchBarEvents.OnSearchTermChanged -> onSearchTermChanged(
                event.searchTerm
            )

            MarketplaceContract.Event.SearchBarEvents.OnDeleteIconClicked -> onDeleteIconClicked()
        }
    }

    private fun init() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            when (val interactorResult = marketplaceInteractor.getMarketplaceInfo()) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {

                }

                is InteractorPartialState.Success -> {
                    val marketplaceUi = marketplaceUiMapper.toUi(interactorResult.data)
                    if (marketplaceUi == null) {
                        setState {
                            copy(
                                isLoading = false,
                            )
                        }
                        // TODO: show error
                        return@launch
                    }

                    setState {
                        copy(
                            isLoading = false,
                            marketplaceUi = marketplaceUi
                        )
                    }
                }
            }
        }
    }

    private fun onPromoCarouselScrolled(newIndex: Int) {
        setState {
            copy(
                marketplaceUi = marketplaceUi?.copy(
                    promo = marketplaceUi.promo?.copy(
                        selectedListingIndex = newIndex
                    )
                )
            )
        }
    }

    private fun onCategoryClicked(categoryCode: String) {
        setEffect {
            MarketplaceContract.Effect.Navigation.GoToListingsByCategoryScreen(
                categoryCode = categoryCode
            )
        }
    }

    private fun onListingClicked(listingId: String) {
        setEffect {
            MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen(
                listingId = listingId
            )
        }
    }

    private fun onSearchTermChanged(newSearchTerm: String) {
        setState {
            copy(
                searchBarTerm = newSearchTerm
            )
        }

        // TODO: Make API call.
    }

    private fun onDeleteIconClicked() {
        setState {
            copy(
                searchBarTerm = ""
            )
        }

        // TODO: Make API call.
    }
}
