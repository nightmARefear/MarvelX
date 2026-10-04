package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen

internal object ListingDetailsTestTags {
    // ListingDetailsContract.Event.OnBack
    const val BACK_BUTTON = "listing_details_back_button"

    // ListingDetailsContract.Event.ToolbarActionButton.OnEditClicked (seller only)
    const val EDIT_BUTTON = "listing_details_edit_button"

    // ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked (buyer only; the same tag is
    // used for the `Favorite` and `Unfavorite` states)
    const val FAVORITE_BUTTON = "listing_details_favorite_button"

    // ListingDetailsContract.Event.ToolbarActionButton.OnShareClicked
    const val SHARE_BUTTON = "listing_details_share_button"

    // ListingDetailsContract.Event.OnStickyBottomButtonClicked (`Add to cart` / `Remove from cart` /
    // `Manage order item` / `View order item details`)
    const val STICKY_BOTTOM_BUTTON = "listing_details_sticky_bottom_button"

    // ListingDetailsContract.Event.OnErrorRetryButtonClicked
    const val ERROR_RETRY_BUTTON = "listing_details_error_retry_button"

    // ListingDetailsContract.Event.OnOrderItemStatusContainerClickableTextClicked
    const val ORDER_ITEM_STATUS_LINK = "listing_details_order_item_status_link"

    // ListingDetailsContract.Event.OnSellerInfoClicked
    const val SELLER_INFO_SECTION = "listing_details_seller_info_section"

    // Non-interactive; the `Final Sale` / `Not Final Sale` row in the details section
    const val FINAL_SALE_ITEM = "listing_details_final_sale_item"
}
