package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain

data class MarketplaceCategorySectionDomain(
    val title: String,
    val categoryCode: String,
    val listings: List<ListingPreviewDomain>
)
