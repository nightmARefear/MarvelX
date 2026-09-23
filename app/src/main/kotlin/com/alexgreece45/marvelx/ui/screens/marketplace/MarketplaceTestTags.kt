package com.alexgreece45.marvelx.ui.screens.marketplace

/**
 * Tags for the marketplace (discover and search modes, plus the sort, filter and comparison bottom
 * sheets). Lists are keyed by listing id, category code, sort / date-range name or option code.
 */
internal object MarketplaceTestTags {
    // MarketplaceContract.Event.OnBackClicked (search mode only)
    const val BACK_BUTTON = "marketplace_back_button"

    // MarketplaceContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "marketplace_error_retry_button"

    // MarketplaceContract.Event.SearchBar.OnSearchTermChanged
    const val SEARCH_TEXT_FIELD = "marketplace_search_text_field"

    // MarketplaceContract.Event.SearchBar.OnDeleteIconClicked
    const val SEARCH_CLEAR_BUTTON = "marketplace_search_clear_button"

    // MarketplaceContract.Event.SearchBar.OnClicked (discover mode; opens the search mode)
    const val SEARCH_BAR_BUTTON = "marketplace_search_bar_button"

    // MarketplaceContract.Event.OnComparisonStatusContainerButtonClicked
    const val COMPARISON_STATUS_BUTTON = "marketplace_comparison_status_button"

    // MarketplaceContract.Event.OnSortChipClicked
    const val SORT_CHIP = "marketplace_sort_chip"

    // MarketplaceContract.Event.OnFiltersChipClicked
    const val FILTERS_CHIP = "marketplace_filters_chip"

    // MarketplaceContract.Event.OnLayoutChangeButtonClicked
    const val LAYOUT_CHANGE_BUTTON = "marketplace_layout_change_button"

    // Sort bottom sheet: MarketplaceContract.Event.SortByBottomSheet.OnCloseButtonClicked
    const val SORT_SHEET_CLOSE_BUTTON = "marketplace_sort_sheet_close_button"

    // Filter menu bottom sheet: MarketplaceContract.Event.FilterMenu.OnCloseButtonClicked
    const val FILTER_SHEET_CLOSE_BUTTON = "marketplace_filter_sheet_close_button"

    // MarketplaceContract.Event.FilterMenu.OnNegativeButtonClicked (clear filters)
    const val FILTER_SHEET_CLEAR_BUTTON = "marketplace_filter_sheet_clear_button"

    // MarketplaceContract.Event.FilterMenu.OnPositiveButtonClicked (apply filters)
    const val FILTER_SHEET_APPLY_BUTTON = "marketplace_filter_sheet_apply_button"

    // MarketplaceContract.Event.Filter.OnPriceRangeMinChanged
    const val FILTER_PRICE_MIN_TEXT_FIELD = "marketplace_filter_price_min_text_field"

    // MarketplaceContract.Event.Filter.OnPriceRangeMaxChanged
    const val FILTER_PRICE_MAX_TEXT_FIELD = "marketplace_filter_price_max_text_field"

    // MarketplaceContract.Event.Filter.DatePosted.OnDatePostedCustomRangeClicked
    const val FILTER_CUSTOM_DATE_RANGE_BUTTON = "marketplace_filter_custom_date_range_button"

    // MarketplaceContract.Event.Filter.OnFinalSaleChecked
    const val FILTER_FINAL_SALE_CHECKBOX = "marketplace_filter_final_sale_checkbox"

    // Comparison bottom sheet: MarketplaceContract.Event.ComparisonBottomSheet.OnCloseButtonClicked
    const val COMPARISON_SHEET_CLOSE_BUTTON = "marketplace_comparison_sheet_close_button"

    // MarketplaceContract.Event.ComparisonBottomSheet.OnShareButtonClicked
    const val COMPARISON_SHEET_SHARE_BUTTON = "marketplace_comparison_sheet_share_button"

    /**
     * MarketplaceContract.Event.OnActiveCategoryDeleteIconClicked; one tag per active category, keyed
     * by the category code (e.g. `marketplace_active_category_comics_remove_button`).
     */
    fun activeCategoryRemoveButton(categoryCode: String) =
        "marketplace_active_category_${categoryCode}_remove_button"

    /**
     * MarketplaceContract.Event.OnCategoryClicked (discover mode); one tag per category section.
     */
    fun categorySection(categoryCode: String) = "marketplace_category_${categoryCode}_section"

    /**
     * MarketplaceContract.Event.Listing.OnClicked (discover mode promo carousel); keyed by listing id.
     */
    fun promoListing(listingId: String) = "marketplace_promo_listing_${listingId}_item"

    /**
     * MarketplaceContract.Event.Listing.OnClicked (discover mode category rows); keyed by listing id.
     */
    fun previewListing(listingId: String) = "marketplace_preview_listing_${listingId}_item"

    /** MarketplaceContract.Event.Listing.OnClicked (search mode); keyed by listing id. */
    fun listingCard(listingId: String) = "marketplace_listing_${listingId}_card"

    /** MarketplaceContract.Event.Listing.OnFavoriteClicked; keyed by listing id. */
    fun listingFavoriteButton(listingId: String) = "marketplace_listing_${listingId}_favorite_button"

    /** MarketplaceContract.Event.Listing.OnComparisonClicked; keyed by listing id. */
    fun listingComparisonButton(listingId: String) =
        "marketplace_listing_${listingId}_comparison_button"

    /**
     * MarketplaceContract.Event.Listing.OnCartClicked; keyed by listing id. Present in both the
     * `Add to cart` and `Remove from cart` states.
     */
    fun listingCartButton(listingId: String) = "marketplace_listing_${listingId}_cart_button"

    /** MarketplaceContract.Event.OnSortSelected; one tag per `ListingSort` (e.g. `..._price_ascending_item`). */
    fun sortOption(sortName: String) = "marketplace_sort_${sortName.lowercase()}_item"

    /** MarketplaceContract.Event.Filter.OnCategorySelected; keyed by the category code. */
    fun filterCategoryChip(categoryCode: String) = "marketplace_filter_category_${categoryCode}_chip"

    /** MarketplaceContract.Event.Filter.OnConditionSelected; keyed by the condition code. */
    fun filterConditionChip(conditionCode: String) =
        "marketplace_filter_condition_${conditionCode}_chip"

    /**
     * MarketplaceContract.Event.Filter.DatePosted.OnDateRangeOptionSelected; one tag per
     * `ListingDateRangeOptionUi`, keyed by its class name (e.g. `..._last24hours_item`).
     */
    fun filterDatePostedOption(optionName: String) =
        "marketplace_filter_date_posted_${optionName.lowercase()}_item"

    /**
     * Collapse / expand toggle of a filter section, keyed by the section (`category`, `condition`,
     * `price_range`, `date_posted`, `final_sale`).
     */
    fun filterSectionHeader(section: String) = "marketplace_filter_${section}_section_header"

    /** MarketplaceContract.Event.ComparisonBottomSheet.OnListingClicked; keyed by listing id. */
    fun comparisonListingItem(listingId: String) = "marketplace_comparison_listing_${listingId}_item"

    /** MarketplaceContract.Event.ComparisonBottomSheet.OnListingDeleteButtonClicked; keyed by listing id. */
    fun comparisonListingRemoveButton(listingId: String) =
        "marketplace_comparison_listing_${listingId}_remove_button"
}
