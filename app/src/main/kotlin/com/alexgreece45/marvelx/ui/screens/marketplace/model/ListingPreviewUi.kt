package com.alexgreece45.marvelx.ui.screens.marketplace.model

data class ListingPreviewUi(
    val listingId: String,
    val title: String,
    val priceLabel: String,
    val coverImageUrl: String,
    val favorite: Boolean
)