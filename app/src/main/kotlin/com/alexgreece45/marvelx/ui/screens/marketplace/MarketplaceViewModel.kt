package com.alexgreece45.marvelx.ui.screens.marketplace

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.MarketplaceUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

@KoinViewModel
class MarketplaceViewModel(
    private val marketplaceInteractor: MarketplaceInteractor,
    private val marketplaceUiMapper: MarketplaceUiMapper,
    private val timerController: TimerController,
) : BaseViewModel<MarketplaceContract.Event, MarketplaceContract.State, MarketplaceContract.Effect>() {

    override fun setInitialState(): MarketplaceContract.State = MarketplaceContract.State()

    override fun handleEvents(event: MarketplaceContract.Event) {
        when (event) {
            MarketplaceContract.Event.Init -> init()
            is MarketplaceContract.Event.OnPromoCarouselScrolled -> onPromoCarouselScrolled(
                newIndex = event.newIndex,
                scrollMillis = DateUtil.getEpochMillis()
            )

            is MarketplaceContract.Event.OnCategoryClicked -> onCategoryClicked(event.categoryCode)
            is MarketplaceContract.Event.OnListingClicked -> onListingClicked(event.listingId)
            MarketplaceContract.Event.SearchBar.OnClicked -> onSearchBarClicked()
            is MarketplaceContract.Event.SearchBar.OnSearchTermChanged -> onSearchTermChanged(
                event.searchTerm
            )

            MarketplaceContract.Event.SearchBar.OnDeleteIconClicked -> onDeleteIconClicked()

            MarketplaceContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
        }
    }

    private fun init() {
        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        viewModelScope.launch {
            when (val interactorResult = marketplaceInteractor.getMarketplaceInfo()) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            isLoading = false,
                            hasError = true
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val marketplaceUi = marketplaceUiMapper.toUi(interactorResult.data)
                    if (marketplaceUi == null) {
                        setState {
                            copy(
                                isLoading = false,
                                hasError = true
                            )
                        }
                        return@launch
                    }

                    // We initialize this first just in case to prevent quickly typed search bar terms
                    // from being ignored since the observer isn't intiliazed.
                    initSearchBarTermObserver()

                    setState {
                        copy(
                            isLoading = false,
                            marketplaceUi = marketplaceUi
                        )
                    }

                    initPromoCarouselAutoScroll()
                }
            }
        }
    }

    private fun onPromoCarouselScrolled(newIndex: Int, scrollMillis: Long) {
        setState {
            copy(
                marketplaceUi = marketplaceUi?.copy(
                    promo = marketplaceUi.promo?.copy(
                        selectedListingIndex = newIndex
                    )
                ),
                lastPromoSectionScrollMillis = scrollMillis
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

    private fun onSearchBarClicked() {

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

    private fun onErrorButtonClicked() {
        init() // TODO: If search mode is the current one, another operation should be performed.
    }

    // Helper functions.

    private fun initPromoCarouselAutoScroll() {
        viewModelScope.launch {
            val delayMillis = 8000L
            timerController.startIndefiniteCountdown(
                delayMillis = delayMillis,
                startImmediately = false
            ).collect {
                val currentMillis = DateUtil.getEpochMillis()
                if (currentMillis - currentState.lastPromoSectionScrollMillis > delayMillis) {
                    currentState.marketplaceUi?.promo?.let { promo ->
                        with(promo) {
                            val newIndex = selectedListingIndex + 1
                            if (newIndex > listings.lastIndex) {
                                onPromoCarouselScrolled(
                                    newIndex = 0,
                                    scrollMillis = currentMillis
                                )
                            } else {
                                onPromoCarouselScrolled(
                                    newIndex = newIndex,
                                    scrollMillis = currentMillis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun initSearchBarTermObserver() {
        viewModelScope.launch {
            uiState // TODO: Handle blank values.
                .map { it.searchBarTerm }
                .distinctUntilChanged()
                .debounce(300.milliseconds)
                .collectLatest { newSearchBarTerm ->
                    // TODO: fetch listings.
                }
        }
    }
}