package com.alexgreece45.marvelx.core.data.model.marketplace

data class MarketplaceSearchCriteria(
    val categoryCodes: List<String>?,
    val conditionCodes: List<String>?,
    val minPrice: String?,
    val maxPrice: String?,
    val datePosted: ListingDateRangeOption,
    val datedPostedCustomRange: ListingDatePostedCustomRange? = null,
    val finalSale: Boolean
)