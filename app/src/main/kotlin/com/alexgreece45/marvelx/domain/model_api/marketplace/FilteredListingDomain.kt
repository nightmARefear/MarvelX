package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.core.data.repositories.marketplace.listing.model.FilteredListing
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import java.math.BigDecimal
import java.time.Instant

data class FilteredListingDomain(
    val listingId: String,
    val category: EnumGeneric,
    val condition: EnumGeneric,
    val title: String,
    val price: BigDecimal,
    val coverImageUrl: String,
    val postedAt: Instant,
    val isFinalSale: Boolean,
    val favorite: Boolean,
    val cart: Boolean
)

fun FilteredListing.toDomain(
    mapCategory: (String) -> EnumGeneric,
    mapCondition: (String) -> EnumGeneric
): FilteredListingDomain =
    FilteredListingDomain(
        listingId = listingId,
        category = mapCategory(categoryCode),
        condition = mapCondition(conditionCode),
        title = title,
        price = price.toBigDecimal(),
        coverImageUrl = coverImageUrl,
        postedAt = Instant.parse(postedAt),
        isFinalSale = isFinalSale,
        favorite = favorite,
        cart = cart
    )