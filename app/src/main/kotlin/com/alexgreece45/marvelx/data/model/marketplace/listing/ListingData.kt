package com.alexgreece45.marvelx.data.model.marketplace.listing

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.details.ResponseListingDetails

/**
 * The [ResponseListing] model of the data layer.
 */
data class ListingData(
    val listingId: String,
    val sellerUserId: String,
    val categoryCode: String,
    val subcategoryCode: String,
    val conditionCode: String,
    val statusCode: String,
    val title: String,
    val description: String,
    val price: String,
    val coverImageUrl: String,
    val imageUrls: List<String>,
    val details: ResponseListingDetails,
    val favorite: Boolean,
    val cart: Boolean,
    val postedAt: String,
    val updatedAt: String?
)
