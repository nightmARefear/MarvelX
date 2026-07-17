package com.alexgreece45.marvelx.core.data.model.marketplace.listing

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview


data class ListingPreviewData(
    val listingId: String,
    val title: String,
    val price: String,
    val coverImageUrl: String,
    val favorite: Boolean
)

fun ResponseListingPreview.toData(): ListingPreviewData =
    ListingPreviewData(
        listingId = listingId,
        title = title,
        price = price,
        coverImageUrl = coverImageUrl,
        favorite = favorite
    )