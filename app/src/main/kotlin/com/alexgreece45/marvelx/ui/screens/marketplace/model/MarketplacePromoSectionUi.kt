package com.alexgreece45.marvelx.ui.screens.marketplace.model

import kotlinx.collections.immutable.ImmutableList

data class MarketplacePromoSectionUi(
    val title: String,
    val listings: ImmutableList<ListingPreviewUi>,
    val selectedListingIndex: Int = 0
)