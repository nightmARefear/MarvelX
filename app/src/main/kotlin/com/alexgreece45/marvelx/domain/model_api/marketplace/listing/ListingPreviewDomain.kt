package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import java.math.BigDecimal

data class ListingPreviewDomain(
    val id: String,
    val sellerUserId: String,
    val title: String,
    val description: String?,
    val price: BigDecimal,
    val coverImageUrl: String
)