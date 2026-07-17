package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.domain.model_api.marketplace.MarketplaceCategorySectionDomain

data class MarketplaceDomain(
    val promo: MarketplacePromoSectionDomain?,
    val categories: List<MarketplaceCategorySectionDomain>
)
