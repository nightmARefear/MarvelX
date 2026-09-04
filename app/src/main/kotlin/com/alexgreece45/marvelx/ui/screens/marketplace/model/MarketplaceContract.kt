package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.core.data.model.marketplace.ListingSort
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.model_generic.UiState
import kotlinx.collections.immutable.persistentListOf

internal sealed interface MarketplaceContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnRefresh : Event
        data class OnPromoCarouselScrolled(val newIndex: Int) : Event
        data class OnCategoryClicked(val categoryCode: String) : Event

        sealed class Listing(open val listingId: String) : Event {
            data class OnClicked(override val listingId: String) : Listing(listingId)
            data class OnFavoriteClicked(override val listingId: String) : Listing(listingId)
            data class OnComparisonClicked(override val listingId: String) : Listing(listingId)
            data class OnCartClicked(override val listingId: String) : Listing(listingId)
        }

        sealed interface SearchBar : Event {
            data object OnClicked : SearchBar
            data class OnSearchTermChanged(val searchTerm: String) : SearchBar
            data object OnDeleteIconClicked : SearchBar
        }

        data object OnSortChipClicked : Event
        data object OnFiltersChipClicked : Event
        data object OnLayoutChangeButtonClicked : Event

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
            data object OnScrolled : ComparisonBottomSheet
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

        val comparisonListings: List<FilteredListingUi> = persistentListOf(),
        val currentCarouselPageIndex: Int = 0,

        val bottomSheetType: MarketplaceBottomSheetType? = null,
    ) : BaseState {

        val carouselPageCount: Int
            get() = comparisonListings.size.let { count ->
                if (count > 1) count - 1 else 1
            }

        val currentCarouselItemIndices: Pair<Int?, Int?>
            get() = when (val comparisonListingsCount = comparisonListings.size) {
                0 -> Pair(null, null)
                1 -> Pair(0, null)
                else -> Pair(
                    first = comparisonListingsCount - 2,
                    second = comparisonListingsCount - 1
                )
            }
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class GoToListingDetailsScreen(val listingId: String) : Navigation
            data object GoToFavoritesScreen : Navigation
            data object GoToCartScreen : Navigation
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
    }
}