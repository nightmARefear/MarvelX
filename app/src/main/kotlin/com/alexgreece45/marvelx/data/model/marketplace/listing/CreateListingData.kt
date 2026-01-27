package com.alexgreece45.marvelx.data.model.marketplace.listing

import java.math.BigDecimal

data class CreateListingData(
    val categoryCode: String,
    val subcategoryCode: String,
    val conditionCode: String,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val coverImageUrl: String,
    val imageUrls: List<String>
)
