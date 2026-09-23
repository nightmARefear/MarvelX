package com.alexgreece45.marvelx.ui.screens.myListings

internal object MyListingsTestTags {
    // MyListingsContract.Event.OnBackClicked
    const val BACK_BUTTON = "my_listings_back_button"

    // MyListingsContract.Event.OnFabButtonClicked
    const val CREATE_LISTING_FAB = "my_listings_create_listing_fab"

    // MyListingsContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "my_listings_error_retry_button"

    // MyListingsContract.Event.OnCreateListingButtonClicked
    const val CREATE_LISTING_BUTTON = "my_listings_create_listing_button"

    // MyListingsContract.Event.BottomSheet.OnEditListingClicked
    const val EDIT_LISTING_BOTTOM_SHEET_ITEM = "my_listings_edit_listing_bottom_sheet_item"

    // MyListingsContract.Event.BottomSheet.OnShareListingClicked
    const val SHARE_LISTING_BOTTOM_SHEET_ITEM = "my_listings_share_listing_bottom_sheet_item"

    // MyListingsContract.Event.BottomSheet.OnDeleteListingClicked
    const val DELETE_LISTING_BOTTOM_SHEET_ITEM = "my_listings_delete_listing_bottom_sheet_item"

    // MyListingsContract.Event.OnDeleteListingDialogPositiveButtonClicked / NegativeButtonClicked
    // use the shared alert dialog tags (`alert_dialog_confirm_button`, `alert_dialog_dismiss_button`).

    /**
     * MyListingsContract.Event.ListingEvents.OnClicked; one tag per listing, keyed by the listing id
     * (e.g. `my_listings_listing_42_card`).
     */
    fun listingCard(listingId: String) = "my_listings_listing_${listingId}_card"

    /**
     * MyListingsContract.Event.ListingEvents.OnMoreActionsButtonClicked; one tag per listing.
     */
    fun listingMoreActionsButton(listingId: String) =
        "my_listings_listing_${listingId}_more_actions_button"
}
