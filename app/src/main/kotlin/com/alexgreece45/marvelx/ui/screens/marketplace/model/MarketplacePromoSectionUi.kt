package com.alexgreece45.marvelx.ui.screens.marketplace.model

data class MarketplacePromoSectionUi(
    val title: String,
    val listings: List<ListingPreviewUi>,
    val selectedListingIndex: Int = 0
)