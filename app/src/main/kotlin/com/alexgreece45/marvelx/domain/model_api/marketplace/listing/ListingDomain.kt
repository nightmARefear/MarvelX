package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.model.model.user.UserId
import java.math.BigDecimal
import java.time.Instant

data class ListingDomain(
    val listingId: String,
    val sellerUserId: UserId,
    val categoryCode: String,
    val subcategoryCode: String,
    val conditionCode: String,
    val status: ListingStatusDomain,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val coverImageUrl: String,
    val imageUrls: List<String>,
    val details: ListingDetailsDomain,
    val postedAt: Instant,
    val updatedAt: Instant?,
    val favorite: Boolean,
    val cart: Boolean,
    val orderIds: ListingOrderIdsDomain?
)