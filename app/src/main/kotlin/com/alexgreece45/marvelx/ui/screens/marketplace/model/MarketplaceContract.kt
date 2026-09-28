package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState

internal sealed interface MarketplaceContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnRefresh : Event
        data object OnNotificationsClicked : Event
        data class OnPromoCarouselScrolled(val newIndex: Int) : Event
        data class OnCategoryClicked(val categoryCode: String) : Event

        sealed class Listing(open val listingId: String) : Event {
            data class OnClicked(override val listingId: String) : Listing(listingId)
            data class OnFavoriteClicked(override val listingId: String) : Listing(listingId)
            data class OnComparisonClicked(override val listingId: String, val category: EnumGeneric) : Listing(listingId)
            data class OnCartClicked(override val listingId: String) : Listing(listingId)
        }

        sealed interface SearchBar : Event {
            data object OnClicked : SearchBar
            data class OnSearchTermChanged(val searchTerm: String) : SearchBar
            data object OnDeleteIconClicked : SearchBar
        }

        data object OnComparisonStatusContainerButtonClicked : Event

        data object OnSortChipClicked : Event
        data object OnFiltersChipClicked : Event
        data object OnLayoutChangeButtonClicked : Event

        data class OnActiveCategoryDeleteIconClicked(val deletedCategory: EnumGeneric) : Event

        sealed interface SortByBottomSheet : Event {
            data object OnCloseButtonClicked : SortByBottomSheet
            data class OnSortSelected(val listingSort: ListingSort) : SortByBottomSheet
        }

        sealed interface FilterMenu : Event {
            data object OnCloseButtonClicked : FilterMenu
            data object OnNegativeButtonClicked : FilterMenu
            data object OnPositiveButtonClicked : FilterMenu
        }

        sealed interface ComparisonBottomSheet : Event {
            data object OnCloseButtonClicked : ComparisonBottomSheet
            data object OnShareButtonClicked : ComparisonBottomSheet
            data class OnListingClicked(val listingId: String) : ComparisonBottomSheet
            data class OnListingDeleteButtonClicked(val listingId: String) : ComparisonBottomSheet
            data class OnScrolled(val page: Int) : ComparisonBottomSheet
        }

        sealed interface Filter : Event {
            data class OnCategorySelected(val code: String) : Filter
            data class OnConditionSelected(val code: String) : Filter
            data class OnPriceRangeMinChanged(val newPrice: String) : Filter
            data class OnPriceRangeMaxChanged(val newPrice: String) : Filter

            sealed interface DatePosted : Filter {
                data class OnDateRangeOptionSelected(val listingDateRangeOption: ListingDateRangeOptionUi) :
                    DatePosted

                data class OnRangeSelected(val minDateMillis: Long, val maxDateMillis: Long) :
                    DatePosted

                data object OnDatePostedCustomRangeClicked : DatePosted
            }

            data object OnFinalSaleChecked : Filter
        }

        data object OnBottomSheetDismissed : Event

        data object OnErrorButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val hasError: Boolean = false,

        val marketplaceUi: MarketplaceUi? = null,

        val lastPromoSectionScrollMillis: Long = 0L,

        val mode: MarketplaceMode = MarketplaceMode.DISCOVER,
        val listingsLayout: MarketplaceListingsLayout = MarketplaceListingsLayout.GRID,

        // -- START OF `Searching` --
        val searchUiState: UiState<List<FilteredListingUi>, Unit> = UiState.Loading,

        val searchQuery: String = "",

        val activeSearchCriteria: MarketplaceSearchCriteriaUi = MarketplaceSearchCriteriaUi(),
        val draftSearchCriteria: MarketplaceSearchCriteriaUi? = null,

        val activeSort: ListingSort = ListingSort.DATE_POSTED_DESCENDING,
        // -- END OF `Searching` --

        val comparison: MarketplaceComparisonUi = MarketplaceComparisonUi(),

        val bottomSheetType: MarketplaceBottomSheetType? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
            data object GoToFavoritesScreen : Navigation
            data object GoToCartScreen : Navigation
            data object GoToNotificationsScreen : Navigation
        }

        sealed interface Share : Effect {
            data class Filters(val listingIds: List<String>) : Share // TODO: Re-define.
            data class Comparison(val listingIds: List<String>) : Share
        }

        data class ShowDateRangePicker(
            val selectedDateRange: Pair<Long, Long>?,
            val minDateMillis: Long,
            val maxDateMillis: Long,
            val title: String,
            val negativeButtonLabel: String,
            val positiveButtonLabel: String
        ) : Effect

        data object HideBottomSheet : Effect
        data object ShowBottomSheet : Effect

        data object RequestSearchBarFieldFocus : Effect

        data class ShowToastMessage(val message: String) : Effect
    }
}