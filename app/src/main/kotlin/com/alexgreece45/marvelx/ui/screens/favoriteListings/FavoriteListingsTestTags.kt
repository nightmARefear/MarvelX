package com.alexgreece45.marvelx.ui.screens.favoriteListings

internal object FavoriteListingsTestTags {
    // FavoriteListingsContract.Event.OnRetryClicked
    const val ERROR_RETRY_BUTTON = "favorite_listings_error_retry_button"

    /**
     * FavoriteListingsContract.Event.OnListingClicked; one tag per listing, keyed by the listing id
     * (e.g. `favorite_listings_listing_42_card`).
     */
    fun listingCard(listingId: String) = "favorite_listings_listing_${listingId}_card"

    /**
     * FavoriteListingsContract.Event.OnFavoriteClicked; one tag per listing.
     */
    fun listingFavoriteButton(listingId: String) =
        "favorite_listings_listing_${listingId}_favorite_button"

    /**
     * FavoriteListingsContract.Event.OnCartClicked; one tag per listing. Present in both the
     * `Add to cart` and `Remove from cart` states.
     */
    fun listingCartButton(listingId: String) = "favorite_listings_listing_${listingId}_cart_button"
}
