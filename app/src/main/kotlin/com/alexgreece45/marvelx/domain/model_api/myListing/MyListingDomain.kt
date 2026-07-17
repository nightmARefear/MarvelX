package com.alexgreece45.marvelx.domain.model_api.myListing

import com.alexgreece45.marvelx.core.data.model.myListing.MyListingData
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingStatusDomain

data class MyListingDomain(
    val listingId: String,
    val title: String,
    val coverImageUrl: String,
    val listingStatus: ListingStatusDomain
)

fun MyListingData.toDomain(): MyListingDomain =
    MyListingDomain(
        listingId = listingId,
        title = title,
        coverImageUrl = coverImageUrl,
        listingStatus = ListingStatusDomain.entries.first { it.code == statusCode }
    )