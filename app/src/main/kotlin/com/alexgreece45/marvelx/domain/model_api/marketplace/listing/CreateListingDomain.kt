package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import java.math.BigDecimal

data class CreateListingDomain(
    val categoryCode: String,
    val subcategoryCode: String,
    val conditionCode: String,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val coverImageUrl: String,
    val imageUrls: List<String>
)
