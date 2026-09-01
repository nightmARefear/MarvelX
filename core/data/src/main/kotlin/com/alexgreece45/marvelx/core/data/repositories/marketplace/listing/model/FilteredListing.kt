package com.alexgreece45.marvelx.core.data.repositories.marketplace.listing.model

import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseFilteredListing

data class FilteredListing(
    val listingId: String,
    val categoryCode: String,
    val conditionCode: String,
    val title: String,
    val price: String,
    val coverImageUrl: String,
    val postedAt: String,
    val isFinalSale: Boolean,
    val favorite: Boolean,
    val cart: Boolean
)

fun ResponseFilteredListing.toData(): FilteredListing =
    FilteredListing(
        listingId = listingId,
        categoryCode = categoryCode,
        conditionCode = conditionCode,
        title = title,
        price = price,
        coverImageUrl = coverImageUrl,
        postedAt = postedAt,
        isFinalSale = isFinalSale,
        favorite = favorite,
        cart = cart
    )