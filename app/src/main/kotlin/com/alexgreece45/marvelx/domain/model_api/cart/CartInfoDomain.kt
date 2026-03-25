package com.alexgreece45.marvelx.domain.model_api.cart

import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain

data class CartInfoDomain(
    val items: List<ListingPreviewDomain>,
    val summary: CartSummaryDomain
)
