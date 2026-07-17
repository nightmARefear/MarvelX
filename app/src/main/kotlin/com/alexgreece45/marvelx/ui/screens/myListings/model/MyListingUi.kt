package com.alexgreece45.marvelx.ui.screens.myListings.model

import com.alexgreece45.marvelx.domain.model_api.myListing.MyListingDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingStatusUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.toUi

/**
 * @param isProcessing Whether this listing is processing -- i.e., a request has been made to the
 * server to delete it.
 */
data class MyListingUi(
    val listingId: String,
    val title: String,
    val coverImageUrl: String,
    val status: ListingStatusUi,
    val isProcessing: Boolean = false
)

fun MyListingDomain.toUi(): MyListingUi =
    MyListingUi(
        listingId = listingId,
        title = title,
        coverImageUrl = coverImageUrl,
        status = listingStatus.toUi(),
    )