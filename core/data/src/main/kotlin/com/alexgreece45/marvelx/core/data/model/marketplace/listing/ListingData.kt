package com.alexgreece45.marvelx.core.data.model.marketplace.listing

import com.alexgreece45.marvelx.core.data.model.order.ListingOrderIds
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.details.ResponseListingDetails
import com.alexgreece45.marvelx.model.model.user.UserId

/**
 * The [ResponseListing] model of the data layer.
 */
data class ListingData(
    val listingId: String,
    val sellerUserId: UserId,
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
    val finalSale: Boolean,
    val postedAt: String,
    val updatedAt: String?,
    val favorite: Boolean,
    val cart: Boolean,
    val orderIds: ListingOrderIds?
)