package com.alexgreece45.marvelx.data.model.myListing

import com.alexgreece45.marvelx.network_logic.models_api.myListing.ResponseMyListing

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
