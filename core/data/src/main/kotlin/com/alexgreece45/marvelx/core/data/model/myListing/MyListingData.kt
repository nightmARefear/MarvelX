package com.alexgreece45.marvelx.core.data.model.myListing

import com.alexgreece45.marvelx.core.network.model.api.myListing.ResponseMyListing

data class MyListingData(
    val listingId: String,
    val title: String,
    val coverImageUrl: String,
    val statusCode: String,
)

fun ResponseMyListing.toData(): MyListingData =
    MyListingData(
        listingId = listingId,
        title = title,
        coverImageUrl = coverImageUrl,
        statusCode = statusCode,
    )