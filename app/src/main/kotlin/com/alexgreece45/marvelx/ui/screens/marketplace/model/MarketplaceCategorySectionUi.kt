package com.alexgreece45.marvelx.ui.screens.marketplace.model

import kotlinx.collections.immutable.ImmutableList

data class MarketplaceCategorySectionUi(
    val title: String,
    val categoryCode: String,
    val listings: ImmutableList<ListingPreviewUi>
)