package com.alexgreece45.marvelx.core.data.model.marketplace

data class MarketplaceSearchCriteria(
    val categoryIds: List<String>,
    val conditionIds: List<String>,
    val minPrice: String?,
    val maxPrice: String?,
    val datePosted: ListingDateRangeOption,
    val datedPostedCustomRange: ListingDatePostedCustomRange? = null,
    val isFinalSale: Boolean
)