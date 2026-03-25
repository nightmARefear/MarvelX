package com.alexgreece45.marvelx.ui.screens.marketplace.model

/**
 * All the required data for the basic details update screen of a listing.
 */
data class UpdateListingBasicDetails(
    val listingId: String,
    val title: String,
    val description: String,
    val categoryLabel: String,
    val subcategoryLabel: String,
    val coverImageUrl: String,
    val imageUrls: List<String> = emptyList()
)
