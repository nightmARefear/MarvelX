package com.alexgreece45.marvelx.ui.screens.marketplace

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.InputFieldValidator
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldRule
import com.alexgreece45.marvelx.common_ui.inputFieldValidator.model.InputFieldValidationResult
import com.alexgreece45.marvelx.common_ui.messageBanner.delegate.MessageBannerDelegate
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerAction
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerActionType
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toEpochDays
import com.alexgreece45.marvelx.core.common.extensions.toEpochMillis
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.onAnyError
import com.alexgreece45.marvelx.domain.helper.onSuccess
import com.alexgreece45.marvelx.domain.interactors.cart.CartInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.model_generic.InputFieldUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.model_generic.UiState
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.FilteredListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.MarketplaceUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.FilteredListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDatePostedCustomRangeUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDateRangeOptionUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceBottomSheetType
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceComparisonUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceListingsLayout
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceSearchCriteriaUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.SelectableOption
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ToggleStateUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.toDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

@KoinViewModel(binds = [MarketplaceViewModel::class])
internal class MarketplaceViewModel(
    private val marketplaceInteractor: MarketplaceInteractor,
    private val marketplaceUiMapper: MarketplaceUiMapper,
    private val timerController: TimerController,
    private val inputFieldValidator: InputFieldValidator,
    private val resourceProvider: ResourceProvider,
    private val listingCategoryRepository: ListingCategoryRepository,
    private val listingDetailsRepository: ListingDetailsRepository,
    private val configPriceLogic: ConfigPriceLogic,
    bottomSheetSnackbarDelegate: SnackbarDelegate,
    private val listingInteractor: ListingInteractor,
    private val filteredListingUiMapper: FilteredListingUiMapper,
    messageBannerDelegate: MessageBannerDelegate,
    private val cartInteractor: CartInteractor,
) : BaseViewModel<MarketplaceContract.Event, MarketplaceContract.State, MarketplaceContract.Effect>(),
    SnackbarDelegate by bottomSheetSnackbarDelegate,
    MessageBannerDelegate by messageBannerDelegate {

    private var promoCarouselAutoScrollJob: Job? = null
    private var searchCriteriaObserverJob: Job? = null

    override fun setInitialState(): MarketplaceContract.State = MarketplaceContract.State()

    override fun handleEvents(event: MarketplaceContract.Event) {
        when (event) {
            MarketplaceContract.Event.Init -> init()
            MarketplaceContract.Event.OnBackClicked -> onBackClicked()
            MarketplaceContract.Event.OnRefresh -> onRefresh()
            MarketplaceContract.Event.OnNotificationsClicked -> setEffect {
                MarketplaceContract.Effect.Navigation.GoToNotificationsScreen
            }
            is MarketplaceContract.Event.OnPromoCarouselScrolled -> onPromoCarouselScrolled(
                newIndex = event.newIndex,
                scrollMillis = DateUtil.getEpochMillis()
            )

            is MarketplaceContract.Event.OnCategoryClicked -> onCategoryClicked(event.categoryCode)

            is MarketplaceContract.Event.Listing -> {
                when (event) {
                    is MarketplaceContract.Event.Listing.OnClicked -> onListingClicked(listingId = event.listingId)
                    is MarketplaceContract.Event.Listing.OnFavoriteClicked -> onListingFavoriteClicked(
                        listingId = event.listingId
                    )

                    is MarketplaceContract.Event.Listing.OnComparisonClicked -> onListingComparisonClicked(
                        listingId = event.listingId,
                        category = event.category
                    )

                    is MarketplaceContract.Event.Listing.OnCartClicked -> onListingAddToCartClicked(
                        listingId = event.listingId
                    )
                }
            }

            MarketplaceContract.Event.SearchBar.OnClicked -> onSearchBarClicked()
            is MarketplaceContract.Event.SearchBar.OnSearchTermChanged -> onSearchBarSearchQueryChanged(
                event.searchTerm
            )

            MarketplaceContract.Event.SearchBar.OnDeleteIconClicked -> onSearchBarDeleteIconClicked()

            MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked -> onComparisonStatusContainerButtonClicked()

            MarketplaceContract.Event.OnSortChipClicked -> onSortChipClicked()
            MarketplaceContract.Event.OnFiltersChipClicked -> onFiltersChipClicked()
            MarketplaceContract.Event.OnLayoutChangeButtonClicked -> onLayoutChangeButtonClicked()

            is MarketplaceContract.Event.OnActiveCategoryDeleteIconClicked -> onActiveCategoryDeleteIconClicked(
                event.deletedCategory
            )

            MarketplaceContract.Event.SortByBottomSheet.OnCloseButtonClicked -> onSortByCloseButtonClicked()
            is MarketplaceContract.Event.SortByBottomSheet.OnSortSelected -> onSortBySortSelected(
                event.listingSort
            )

            MarketplaceContract.Event.FilterMenu.OnCloseButtonClicked -> onFilterMenuCloseButtonClicked()
            MarketplaceContract.Event.FilterMenu.OnNegativeButtonClicked -> onFilterMenuNegativeButtonClicked()
            MarketplaceContract.Event.FilterMenu.OnPositiveButtonClicked -> onFilterMenuPositiveButtonClicked()

            MarketplaceContract.Event.ComparisonBottomSheet.OnCloseButtonClicked -> onComparisonBottomSheetCloseButtonClicked()
            MarketplaceContract.Event.ComparisonBottomSheet.OnShareButtonClicked -> onComparisonBottomSheetShareButtonClicked()
            is MarketplaceContract.Event.ComparisonBottomSheet.OnListingClicked -> onComparisonBottomSheetListingClicked(
                event.listingId
            )

            is MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked -> onComparisonBottomSheetListingDeleteButtonClicked(
                event.listingId
            )

            is MarketplaceContract.Event.ComparisonBottomSheet.OnScrolled -> onComparisonBottomSheetScrolled(
                page = event.page
            )

            is MarketplaceContract.Event.Filter.OnCategorySelected -> onFilterCategorySelected(code = event.code)
            is MarketplaceContract.Event.Filter.OnConditionSelected -> onFilterConditionSelected(
                event.code
            )

            is MarketplaceContract.Event.Filter.OnPriceRangeMinChanged -> onFilterPriceRangeMinChanged(
                event.newPrice
            )

            is MarketplaceContract.Event.Filter.OnPriceRangeMaxChanged -> onFilterPriceRangeMaxChanged(
                event.newPrice
            )

            is MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected -> onFilterDatePostedDateRangeOptionSelected(
                event.listingDateRangeOption
            )

            is MarketplaceContract.Event.Filter.DatePosted.OnRangeSelected -> onFilterDatePostedRangeSelected(
                event.minDateMillis,
                event.maxDateMillis
            )

            MarketplaceContract.Event.Filter.DatePosted.OnDatePostedCustomRangeClicked -> onFilterDatePostedCustomRangeClicked()

            MarketplaceContract.Event.Filter.OnFinalSaleChecked -> onFilterFinalSaleChecked()

            MarketplaceContract.Event.OnBottomSheetDismissed -> onBottomSheetDismissed()

            MarketplaceContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
        }
    }

    private fun init() {
        getMarketplaceInfo(isFromError = false, isFromRefresh = false)
    }

    private fun onBackClicked() {
        if (currentState.mode == MarketplaceMode.SEARCH) {
            setState {
                copy(
                    mode = MarketplaceMode.DISCOVER,
                    listingsLayout = MarketplaceListingsLayout.GRID,
                    searchUiState = UiState.Loading,
                    searchQuery = "",
                    activeSearchCriteria = getResetSearchCriteria(),
                    comparison = MarketplaceComparisonUi(),
                    activeSort = ListingSort.DATE_POSTED_DESCENDING
                )
            }
        }
    }

    private fun onRefresh() {
        if (currentState.isRefreshing) return

        setState {
            copy(
                isRefreshing = true
            )
        }
        when (currentState.mode) {
            MarketplaceMode.DISCOVER -> getMarketplaceInfo(
                isFromError = false,
                isFromRefresh = true
            )

            MarketplaceMode.SEARCH -> getFilteredListings(isFromRefresh = true)
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
        setState {
            copy(
                mode = MarketplaceMode.SEARCH,
                activeSearchCriteria = activeSearchCriteria.copy(
                    categories = activeSearchCriteria.categories.map { category ->
                        if (category.option.code == categoryCode) {
                            category.copy(isSelected = true)
                        } else {
                            category
                        }
                    }
                )
            )
        }
        getFilteredListings(isFromRefresh = false)
    }

    private fun onListingClicked(listingId: String) {
        setEffect {
            MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen(
                listingId = listingId
            )
        }
    }

    private fun onListingFavoriteClicked(listingId: String) {
        var isListingFavorite = false
        setState {
            copy(
                searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                    data = searchUiState.data.map { filteredListing ->
                        if (filteredListing.listingId == listingId) {
                            isListingFavorite = filteredListing.favorite.isChecked

                            filteredListing.copy(favorite = filteredListing.favorite.copy(isLoading = true))
                        } else {
                            filteredListing
                        }
                    }
                )
            )
        }
        viewModelScope.launch {
            if (isListingFavorite) {
                val wasRemovedFromFavorites =
                    listingInteractor.removeFromFavorites(listingId = listingId)
                if (wasRemovedFromFavorites) {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            favorite = ToggleStateUi(
                                                isChecked = false,
                                                isLoading = false
                                            )
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_removed_from_favorites_success),
                        action =
                            MessageBannerAction.Global(
                                label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label),
                                actionType = MessageBannerActionType.Custom(
                                    action = {
                                        setEffect { MarketplaceContract.Effect.Navigation.GoToFavoritesScreen }
                                    }
                                )
                            )
                    )
                } else {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            favorite = filteredListing.favorite.copy(isLoading = false)
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_error_filtered_listing_remove_from_favorites),
                        action = MessageBannerAction.Button(
                            label = resourceProvider.getString(R.string.generic_ok_button),
                            actionType = MessageBannerActionType.Hide
                        )
                    )
                }
            } else {
                val wasAddedToFavorites = listingInteractor.addToFavorites(listingId = listingId)
                if (wasAddedToFavorites) {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            favorite = filteredListing.favorite.copy(
                                                isChecked = true,
                                                isLoading = false
                                            )
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_added_to_favorites_success),
                        action = MessageBannerAction.Global(
                            label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label),
                            actionType = MessageBannerActionType.Custom(
                                action = {
                                    setEffect { MarketplaceContract.Effect.Navigation.GoToFavoritesScreen }
                                }
                            )
                        )
                    )
                } else {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            favorite = filteredListing.favorite.copy(isLoading = false)
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_error_filtered_listing_add_to_favorites),
                        action = MessageBannerAction.Button(
                            label = resourceProvider.getString(R.string.generic_ok_button),
                            actionType = MessageBannerActionType.Hide
                        )
                    )
                }
            }
        }
    }

    private fun onListingComparisonClicked(listingId: String, category: EnumGeneric) {
        val currentComparisonCategory = currentState.comparison.comparisonCategory
        if (currentComparisonCategory != null && currentComparisonCategory.code != category.code) {
            showInfoSnackbar(
                message = resourceProvider.getString(
                    R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_title,
                    currentComparisonCategory.label,
                    category.label
                ),
                snackbarActionData = SnackbarActionData(
                    label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_button),
                    actionType = SnackbarActionType.Custom(
                        onAction = {
                            var comparisonListing: FilteredListingUi? = null
                            setState {
                                copy(
                                    searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                        data = searchUiState.data.map { filteredListing ->
                                            if (filteredListing.listingId != listingId) {
                                                filteredListing.copy(comparison = false)
                                            } else {
                                                comparisonListing =
                                                    filteredListing.copy(comparison = true)
                                                comparisonListing
                                            }
                                        }
                                    ),
                                    comparison = try {
                                        MarketplaceComparisonUi(
                                            listings = listOf(comparisonListing!!), // For some reason wrapping the variable inside a `when` and working on the non-null branch still provides a null version, so this solution is used. The null value is impossible anyways.
                                            comparisonCategory = category,
                                            currentCarouselPageIndex = 0
                                        )
                                    } catch (_: Exception) {
                                        MarketplaceComparisonUi()
                                    }
                                )
                            }
                            showInfoSnackbar(
                                message = resourceProvider.getString(
                                    R.string.screen_marketplace_filtered_listing_added_to_comparison_ineligible_action_executed_message,
                                    category.label
                                ),
                                maxMessageLines = 5
                            )
                        }
                    )
                )
            )
            return
        }

        var wasListingAddedToComparison = false
        var listing: FilteredListingUi? = null
        setState {
            copy(
                searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                    data = searchUiState.data.map { filteredListing ->
                        if (filteredListing.listingId == listingId) {
                            wasListingAddedToComparison = !filteredListing.comparison

                            filteredListing.copy(comparison = wasListingAddedToComparison)
                                .also { listing = it }
                        } else {
                            filteredListing
                        }
                    }
                )
            )
        }

        if (wasListingAddedToComparison && listing != null) {
            setState {
                copy(
                    comparison = comparison.copy(
                        listings = buildList {
                            addAll(comparison.listings)
                            add(listing)
                        },
                        comparisonCategory = category
                    )
                )
            }
        } else {
            val comparisonListings =
                currentState.comparison.listings.filterNot { it.listingId == listingId }
            setState {
                copy(
                    comparison = comparison.copy(
                        listings = comparisonListings,
                        comparisonCategory = if (comparisonListings.isEmpty()) null else comparison.comparisonCategory
                    )
                )
            }
        }

        showMessageBanner(
            message = resourceProvider.getString(if (wasListingAddedToComparison) R.string.screen_marketplace_filtered_listing_added_to_comparison_success else R.string.screen_marketplace_filtered_listing_removed_from_comparison_success),
            action = MessageBannerAction.Global(
                label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label),
                actionType = MessageBannerActionType.Custom(
                    action = {
                        showBottomSheet(MarketplaceBottomSheetType.COMPARISON)
                    }
                )
            )
        )
    }

    private fun onListingAddToCartClicked(listingId: String) {
        var isListingInCart = false
        setState {
            copy(
                searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                    data = searchUiState.data.map { filteredListing ->
                        if (filteredListing.listingId == listingId) {
                            isListingInCart = filteredListing.cart.isChecked

                            filteredListing.copy(cart = filteredListing.cart.copy(isLoading = true))
                        } else {
                            filteredListing
                        }
                    }
                )
            )
        }
        viewModelScope.launch {
            if (isListingInCart) {
                val wasRemovedFromCart =
                    cartInteractor.deleteCartItem(listingId = listingId)
                if (wasRemovedFromCart) {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            cart = ToggleStateUi(
                                                isChecked = false,
                                                isLoading = false
                                            )
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_removed_from_cart_success),
                        action =
                            MessageBannerAction.Global(
                                label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label),
                                actionType = MessageBannerActionType.Custom(
                                    action = {
                                        setEffect { MarketplaceContract.Effect.Navigation.GoToCartScreen }
                                    }
                                )
                            )
                    )
                } else {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            cart = filteredListing.cart.copy(isLoading = false)
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_error_filtered_listing_remove_from_cart),
                        action = MessageBannerAction.Button(
                            label = resourceProvider.getString(R.string.generic_ok_button),
                            actionType = MessageBannerActionType.Hide
                        )
                    )
                }
            } else {
                val wasAddedToCart = cartInteractor.addCartItem(listingId = listingId)
                if (wasAddedToCart) {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            cart = ToggleStateUi(
                                                isChecked = true,
                                                isLoading = false
                                            )
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_added_to_cart_success),
                        action = MessageBannerAction.Global(
                            label = resourceProvider.getString(R.string.screen_marketplace_filtered_listing_image_action_button_success_message_banner_label),
                            actionType = MessageBannerActionType.Custom(
                                action = {
                                    setEffect { MarketplaceContract.Effect.Navigation.GoToCartScreen }
                                }
                            )
                        )
                    )
                } else {
                    setState {
                        copy(
                            searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                                data = searchUiState.data.map { filteredListing ->
                                    if (filteredListing.listingId == listingId) {
                                        filteredListing.copy(
                                            cart = filteredListing.cart.copy(isLoading = false)
                                        )
                                    } else {
                                        filteredListing
                                    }
                                }
                            )
                        )
                    }

                    showMessageBanner(
                        message = resourceProvider.getString(R.string.screen_marketplace_error_filtered_listing_add_to_cart),
                        action = MessageBannerAction.Button(
                            label = resourceProvider.getString(R.string.generic_ok_button),
                            actionType = MessageBannerActionType.Hide
                        )
                    )
                }
            }
        }
    }

    private fun onSearchBarClicked() {
        setState {
            copy(
                marketplaceUi = marketplaceUi?.copy(
                    promo = marketplaceUi.promo?.copy(selectedListingIndex = 0)
                ),
                mode = MarketplaceMode.SEARCH
            )
        }
        setEffect { MarketplaceContract.Effect.RequestSearchBarFieldFocus }
        getFilteredListings(isFromRefresh = false)
    }

    private fun onSearchBarSearchQueryChanged(newSearchQuery: String) {
        setState {
            copy(
                mode = MarketplaceMode.SEARCH, // Re-assigned in the rare case that the phone has as an input method a keyboard and the user types something, taps on "enter" triggering the
                // search to occur which captures the focus of the "close button", and further tapping the "enter" again, for some reason, the back button is triggered resulting into the
                // mode turning into "discover". While in the "discover" mode, the search bar has focus allowing the user to enter queries. Search-as-you-type functionality doesn't work,
                // thus, a solution must be found. The solution is to re-assign the mode when the user types in the search bar.
                searchQuery = newSearchQuery
            )
        }
    }

    private fun onSearchBarDeleteIconClicked() {
        setState {
            copy(
                searchQuery = ""
            )
        }
    }

    private fun onComparisonStatusContainerButtonClicked() {
        showBottomSheet(sheetType = MarketplaceBottomSheetType.COMPARISON)
    }

    private fun onSortChipClicked() {
        showBottomSheet(MarketplaceBottomSheetType.SORT)
    }

    private fun onFiltersChipClicked() {
        showBottomSheet(MarketplaceBottomSheetType.FILTER_MENU)
    }

    private fun onLayoutChangeButtonClicked() {
        setState {
            copy(
                listingsLayout = when (listingsLayout) {
                    MarketplaceListingsLayout.GRID -> MarketplaceListingsLayout.LIST
                    MarketplaceListingsLayout.LIST -> MarketplaceListingsLayout.GRID
                }
            )
        }
    }

    private fun onActiveCategoryDeleteIconClicked(deletedCategory: EnumGeneric) {
        setState {
            copy(
                activeSearchCriteria = activeSearchCriteria.copy(
                    categories = activeSearchCriteria.categories.map { category ->
                        if (category.option.code == deletedCategory.code) {
                            category.copy(isSelected = false)
                        } else {
                            category
                        }
                    }
                )
            )
        }
        showMessageBanner(
            message = resourceProvider.getString(
                R.string.screen_marketplace_message_category_filter_removed,
                deletedCategory.label
            )
        )
    }

    private fun onFilterCategorySelected(code: String) {
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    categories = draftSearchCriteria.categories.map { category ->
                        if (category.option.code == code) {
                            category.copy(isSelected = !category.isSelected)
                        } else {
                            category
                        }
                    }
                )
            )
        }
    }

    private fun onFilterConditionSelected(code: String) {
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    conditions = draftSearchCriteria.conditions.map { condition ->
                        if (condition.option.code == code) {
                            condition.copy(isSelected = !condition.isSelected)
                        } else {
                            condition
                        }
                    }
                )
            )
        }
    }

    private fun onFilterPriceRangeMinChanged(newPrice: String) {
        val newPrice =
            newPrice.filter { it.isDigit() || it == '.' } // TODO: Second check will cause issues if the locale is Greek as it uses ',' for decimals.
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    priceRangeMin = draftSearchCriteria.priceRangeMin.copy(
                        value = newPrice,
                        validationResult = inputFieldValidator.validateInputSerially(
                            value = newPrice,
                            rules = getPriceRangeMinValidationRules(
                                minValue = newPrice,
                                maxPriceInputField = draftSearchCriteria.priceRangeMax
                            )
                        )
                    ),
                    priceRangeMax = draftSearchCriteria.priceRangeMax.copy( // Re-validated as the max price could be blank.
                        validationResult = inputFieldValidator.validateInputSerially(
                            value = draftSearchCriteria.priceRangeMax.value,
                            rules = getPriceRangeMaxValidationRules(
                                maxValue = draftSearchCriteria.priceRangeMax.value
                                // Parsed value required for `getPriceRangeMaxValidationRules` will be recalculated inside the data class.
                            )
                        )
                    )
                )
            )
        }
    }

    private fun onFilterPriceRangeMaxChanged(newPrice: String) {
        val newPrice =
            newPrice.filter { it.isDigit() || it == '.' } // TODO: Second check will cause issues if the locale is Greek as it uses ',' for decimals.
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    priceRangeMin = draftSearchCriteria.priceRangeMin.copy( // Re-validated as the max price could be blank.
                        validationResult = inputFieldValidator.validateInputSerially(
                            value = draftSearchCriteria.priceRangeMin.value,
                            rules = getPriceRangeMinValidationRules(
                                minValue = draftSearchCriteria.priceRangeMin.value,
                                maxPriceInputField = draftSearchCriteria.priceRangeMax.copy(value = newPrice) // Parsed value required for `getPriceRangeMinValidationRules` will be recalculated inside the data class.
                            )
                        )
                    ),
                    priceRangeMax = draftSearchCriteria.priceRangeMax.copy(
                        value = newPrice,
                        validationResult = inputFieldValidator.validateInputSerially(
                            value = newPrice,
                            rules = getPriceRangeMaxValidationRules(
                                maxValue = newPrice
                            )
                        )
                    )
                )
            )
        }
    }

    private fun onFilterDatePostedDateRangeOptionSelected(listingDateRangeOption: ListingDateRangeOptionUi) {
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    datePosted = listingDateRangeOption,
                    datedPostedCustomRange = if (listingDateRangeOption !is ListingDateRangeOptionUi.Custom) null else draftSearchCriteria.datedPostedCustomRange
                )
            )
        }
    }

    private fun onFilterDatePostedRangeSelected(minDateMillis: Long, maxDateMillis: Long) {
        val minDateLocalDate = LocalDate.ofEpochDay(minDateMillis.toEpochDays())
        val maxDateLocalDate = LocalDate.ofEpochDay(maxDateMillis.toEpochDays())
        val dateFormatterPattern = "dd/MM/yyyy"

        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    datedPostedCustomRange = ListingDatePostedCustomRangeUi(
                        from = LocalDateUi(
                            value = minDateLocalDate,
                            label = minDateLocalDate.toCustomFormat(dateFormatterPattern)
                        ),
                        to = LocalDateUi(
                            value = maxDateLocalDate,
                            label = maxDateLocalDate.toCustomFormat(dateFormatterPattern)
                        )
                    )
                )
            )
        }
    }

    private fun onFilterDatePostedCustomRangeClicked() {
        showDateRangePicker()
    }

    private fun onFilterFinalSaleChecked() {
        setState {
            copy(
                draftSearchCriteria = draftSearchCriteria?.copy(
                    finalSale = !draftSearchCriteria.finalSale
                )
            )
        }
    }

    private fun onBottomSheetDismissed() {
        setState {
            copy(
                bottomSheetType = null,
                comparison = comparison.copy(currentCarouselPageIndex = 0),// The carousel page index is reset so when the carousel is reopened, the user sees the first listings.
            )
        }
    }

    private fun onSortByCloseButtonClicked() {
        hideBottomSheet()
    }

    private fun onSortBySortSelected(listingSort: ListingSort) {
        hideBottomSheet()
        setState {
            copy(
                activeSort = listingSort
            )
        }
    }

    private fun onFilterMenuCloseButtonClicked() {
        hideBottomSheet()
        setState {
            copy(
                draftSearchCriteria = null
            )
        }
    }

    private fun onFilterMenuNegativeButtonClicked() {
        setState {
            copy(
                draftSearchCriteria = getResetSearchCriteria()
            )
        }
        setEffect {
            MarketplaceContract.Effect.ShowToastMessage(resourceProvider.getString(R.string.screen_marketplace_filter_menu_negative_button_action_message))
        }
    }

    private fun onFilterMenuPositiveButtonClicked() {
        when (val draftSearchCriteria = currentState.draftSearchCriteria) {
            null -> {
                // Should never occur, but just in case.
                hideBottomSheet()
            }

            else -> {
                hideBottomSheet()
                setState {
                    copy(
                        draftSearchCriteria = null,
                        activeSearchCriteria = draftSearchCriteria
                    )
                }
            }
        }
    }

    private fun onComparisonBottomSheetCloseButtonClicked() {
        hideBottomSheet()
    }

    private fun onComparisonBottomSheetShareButtonClicked() {
        val listingIds = currentState.comparison.listings.map { it.listingId }
        if (listingIds.isEmpty()) {
            showMessageBanner(message = resourceProvider.getString(R.string.screen_marketplace_error_sharing_comparison_listings))
        } else {
            setEffect {
                MarketplaceContract.Effect.Share.Comparison(listingIds = listingIds)
            }
        }
    }

    private fun onComparisonBottomSheetListingClicked(listingId: String) {
        hideBottomSheet()
        setEffect { MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen(listingId = listingId) }
    }

    private fun onComparisonBottomSheetListingDeleteButtonClicked(listingId: String) {
        val comparisonListings =
            currentState.comparison.listings.filterNot { it.listingId == listingId }
        setState {
            copy(
                searchUiState = if (searchUiState !is UiState.Success) searchUiState else searchUiState.copy(
                    data = searchUiState.data.map { filteredListing ->
                        if (filteredListing.listingId == listingId) {
                            filteredListing.copy(comparison = false)
                        } else {
                            filteredListing
                        }
                    }
                ),
                comparison = comparison.copy(
                    listings = comparisonListings,
                    comparisonCategory = if (comparisonListings.isEmpty()) null else comparison.comparisonCategory
                )
            )
        }
        if (comparisonListings.isEmpty()) {
            hideBottomSheet()
            showMessageBanner(message = resourceProvider.getString(R.string.screen_marketplace_listings_for_comparison_last_removed))
        }
    }

    private fun onComparisonBottomSheetScrolled(page: Int) {
        setState {
            copy(
                comparison = comparison.copy(currentCarouselPageIndex = page)
            )
        }
    }

    private fun onErrorButtonClicked() {
        if (currentState.hasError) {
            getMarketplaceInfo(isFromError = true, isFromRefresh = false)
        } else if (currentState.searchUiState.isError) {
            getFilteredListings(isFromRefresh = false)
        }
    }

    // Helper functions.

    private fun getMarketplaceInfo(isFromError: Boolean, isFromRefresh: Boolean) {
        setState {
            copy(
                isLoading = true,
                hasError = false
            ).setIsRefreshing(isRefreshing = true, isFromRefresh = isFromRefresh)
        }

        if (isFromError) {
            promoCarouselAutoScrollJob?.cancel()
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
                        ).setIsRefreshing(isRefreshing = false, isFromRefresh = isFromRefresh)
                    }
                }

                is InteractorPartialState.Success -> {
                    val marketplaceUi = marketplaceUiMapper.toUi(interactorResult.data)
                    if (marketplaceUi == null) {
                        setState {
                            copy(
                                isLoading = false,
                                hasError = false
                            ).setIsRefreshing(isRefreshing = false, isFromRefresh = isFromRefresh)
                        }
                        return@launch
                    }

                    // Only initialize this job if it hasn't been initialized before. The past job
                    // can be perfectly used.
                    if (searchCriteriaObserverJob == null) {
                        // We initialize this first just in case to prevent quickly typed search bar terms
                        // from being ignored since the observer isn't intiliazed.
                        initSearchCriteriaObserver()
                    }

                    setState {
                        copy(
                            isLoading = false,
                            marketplaceUi = marketplaceUi,
                            activeSearchCriteria = activeSearchCriteria.copy(
                                categories = listingCategoryRepository.listingCategories.map { category ->
                                    SelectableOption(
                                        option = EnumGeneric(
                                            code = category.code,
                                            label = category.label
                                        ),
                                        isSelected = false
                                    )
                                },
                                conditions = listingDetailsRepository.listingConditions.map { condition ->
                                    SelectableOption(
                                        option = condition,
                                        isSelected = false
                                    )
                                },
                                priceRangeMin = activeSearchCriteria.priceRangeMin.copy(
                                    priceScale = configPriceLogic.priceScale,
                                    roundingMode = configPriceLogic.roundingMode
                                ),
                                priceRangeMax = activeSearchCriteria.priceRangeMax.copy(
                                    priceScale = configPriceLogic.priceScale,
                                    roundingMode = configPriceLogic.roundingMode
                                )
                            )
                        ).setIsRefreshing(isRefreshing = false, isFromRefresh = isFromRefresh)
                    }

                    initPromoCarouselAutoScroll()
                }
            }
        }
    }

    private fun initPromoCarouselAutoScroll() {
        promoCarouselAutoScrollJob?.cancel() // Cancels previous job in order to avoid multiple jobs being present in the same time.
        promoCarouselAutoScrollJob = viewModelScope.launch {
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
    private fun initSearchCriteriaObserver() {
        searchCriteriaObserverJob = viewModelScope.launch {
            uiState
                .map { state ->
                    SearchTriggerSnapshot(
                        searchQuery = state.searchQuery,
                        searchCriteria = state.activeSearchCriteria,
                        sort = state.activeSort
                    )
                }
                .distinctUntilChanged()
                .debounce(timeout = 300.milliseconds)
                .collectLatest { _ -> getFilteredListings(isFromRefresh = false) }
        }
    }

    private fun getFilteredListings(isFromRefresh: Boolean) {
        setState {
            copy(
                searchUiState = UiState.Loading,
                comparison = MarketplaceComparisonUi(),
            ).setIsRefreshing(isRefreshing = true, isFromRefresh = isFromRefresh)
        }
        viewModelScope.launch {
            currentState.activeSearchCriteria.toDomain()
                .onSuccess { searchCriteria ->
                    listingInteractor.getFilteredListings(
                        searchQuery = currentState.searchQuery,
                        searchCriteria = searchCriteria,
                        sort = currentState.activeSort
                    )
                        .onSuccess { data ->
                            val filteredListingsUi = filteredListingUiMapper.toUiList(
                                listings = data
                            )
                            if (filteredListingsUi == null) {
                                setState {
                                    copy(
                                        searchUiState = UiState.Error(Unit)
                                    ).setIsRefreshing(
                                        isRefreshing = false,
                                        isFromRefresh = isFromRefresh
                                    )
                                }
                            } else {
                                setState {
                                    copy(
                                        searchUiState = UiState.Success(filteredListingsUi)
                                    ).setIsRefreshing(
                                        isRefreshing = false,
                                        isFromRefresh = isFromRefresh
                                    )
                                }
                            }
                        }
                        .onAnyError {
                            setState {
                                copy(
                                    searchUiState = UiState.Error(Unit)
                                ).setIsRefreshing(
                                    isRefreshing = false,
                                    isFromRefresh = isFromRefresh
                                )
                            }
                        }
                }
                .onFailure {
                    setState {
                        copy(
                            searchUiState = UiState.Error(Unit)
                        ).setIsRefreshing(isRefreshing = false, isFromRefresh = isFromRefresh)
                    }
                }
        }
    }

    private fun getPriceRangeMinValidationRules(
        minValue: String,
        maxPriceInputField: InputFieldUi.Price
    ): List<InputFieldRule> {
        if (minValue.isBlank()) return emptyList()
        val maxBigDecimal = maxPriceInputField.parsedValue ?: configPriceLogic.maximumPrice

        return listOf(
            InputFieldRule.ValidateBigDecimal(
                errorMessage = resourceProvider.getString(R.string.screen_marketplace_error_invalid_price)
            ),
            InputFieldRule.ValidateOverMinimumBigDecimalValue(
                minValue = configPriceLogic.minimumPrice,
                errorMessage = resourceProvider.getString(
                    R.string.screen_marketplace_error_below_min_price,
                    configPriceLogic.minimumPrice.formatPrice(
                        configPriceLogic.priceScale,
                        configPriceLogic.roundingMode
                    )
                )
            ),
            InputFieldRule.ValidateUnderMaximumBigDecimalValue(
                maxValue = maxBigDecimal,
                errorMessage = resourceProvider.getString(
                    R.string.screen_marketplace_error_over_max_price,
                    maxBigDecimal.formatPrice(
                        configPriceLogic.priceScale,
                        configPriceLogic.roundingMode
                    )
                )
            )
        )
    }

    private fun getPriceRangeMaxValidationRules(maxValue: String): List<InputFieldRule> {
        if (maxValue.isBlank()) return emptyList()

        return listOf(
            InputFieldRule.ValidateBigDecimal(
                errorMessage = resourceProvider.getString(R.string.screen_marketplace_error_invalid_price)
            ),
            InputFieldRule.ValidateUnderMaximumBigDecimalValue(
                maxValue = configPriceLogic.maximumPrice,
                errorMessage = resourceProvider.getString(
                    R.string.screen_marketplace_error_over_max_price,
                    configPriceLogic.maximumPrice.formatPrice(
                        configPriceLogic.priceScale,
                        configPriceLogic.roundingMode
                    )
                )
            ),
            InputFieldRule.ValidateOverMinimumBigDecimalValue(
                minValue = configPriceLogic.minimumPrice,
                errorMessage = resourceProvider.getString(
                    R.string.screen_marketplace_error_below_min_price,
                    configPriceLogic.minimumPrice.formatPrice(
                        configPriceLogic.priceScale,
                        configPriceLogic.roundingMode
                    )
                )
            )
        )
    }

    private fun getResetSearchCriteria(): MarketplaceSearchCriteriaUi {
        return with(currentState.activeSearchCriteria) {
            MarketplaceSearchCriteriaUi(
                categories = categories.map { it.copy(isSelected = false) },
                conditions = conditions.map { it.copy(isSelected = false) },
                priceRangeMin = priceRangeMin.copy(
                    value = "",
                    validationResult = InputFieldValidationResult.Uninitialized
                ),
                priceRangeMax = priceRangeMax.copy(
                    value = "",
                    validationResult = InputFieldValidationResult.Uninitialized
                ),
                datePosted = ListingDateRangeOptionUi.All,
                datedPostedCustomRange = null,
                finalSale = false
            )
        }
    }

    private fun showDateRangePicker() {
        val minDateMillis = LocalDate.of(2026, 1, 1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val maxDateMillis = DateUtil.getCurrentLocalDate()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        setEffect {
            MarketplaceContract.Effect.ShowDateRangePicker(
                selectedDateRange = currentState.draftSearchCriteria?.datedPostedCustomRange?.let {
                    Pair(
                        first = it.from.value.toEpochMillis(),
                        second = it.to.value.toEpochMillis()
                    )
                },
                minDateMillis = minDateMillis,
                maxDateMillis = maxDateMillis,
                title = resourceProvider.getString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_label),
                negativeButtonLabel = resourceProvider.getString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_negative_button),
                positiveButtonLabel = resourceProvider.getString(R.string.screen_marketplace_filter_menu_section_date_posted_custom_range_picker_positive_button),
            )
        }
    }

    private fun showBottomSheet(sheetType: MarketplaceBottomSheetType) {
        when (sheetType) {
            MarketplaceBottomSheetType.SORT -> {
                setState {
                    copy(
                        bottomSheetType = sheetType
                    )
                }
            }

            MarketplaceBottomSheetType.FILTER_MENU -> {
                setState {
                    copy(
                        draftSearchCriteria = activeSearchCriteria,
                        bottomSheetType = sheetType
                    )
                }
            }

            MarketplaceBottomSheetType.COMPARISON -> {
                setState {
                    copy(
                        bottomSheetType = sheetType
                    )
                }
            }
        }
        setEffect { MarketplaceContract.Effect.ShowBottomSheet }
    }

    private fun hideBottomSheet() {
        setState {
            copy(
                bottomSheetType = null
            )
        }
        setEffect { MarketplaceContract.Effect.HideBottomSheet }
    }

    private fun MarketplaceContract.State.setIsRefreshing(
        isRefreshing: Boolean,
        isFromRefresh: Boolean
    ): MarketplaceContract.State {
        return if (isFromRefresh) this.copy(isRefreshing = isRefreshing) else this
    }
}

private data class SearchTriggerSnapshot(
    val searchQuery: String,
    val searchCriteria: MarketplaceSearchCriteriaUi,
    val sort: ListingSort
)