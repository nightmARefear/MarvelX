package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain

data class MarketplacePromoSectionDomain(
    val title: String,
    val listings: List<ListingPreviewDomain>
)
