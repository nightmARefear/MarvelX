package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.core.data.model.marketplace.ListingDatePostedCustomRange
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingDateRangeOption
import com.alexgreece45.marvelx.core.data.model.marketplace.MarketplaceSearchCriteria
import java.math.BigDecimal

data class MarketplaceSearchCriteriaDomain(
    val categoryCodes: List<String>,
    val conditionCodes: List<String>,
    val minPrice: BigDecimal?,
    val maxPrice: BigDecimal?,
    val datePosted: ListingDateRangeOption,
    val datedPostedCustomRange: ListingDatePostedCustomRangeDomain? = null,
    val finalSale: Boolean
)

internal fun MarketplaceSearchCriteria.toDomain(): MarketplaceSearchCriteriaDomain =
    MarketplaceSearchCriteriaDomain(
        categoryCodes = categoryCodes.orEmpty(),
        conditionCodes = conditionCodes.orEmpty(),
        minPrice = minPrice?.toBigDecimal(),
        maxPrice = maxPrice?.toBigDecimal(),
        datePosted = datePosted,
        datedPostedCustomRange = datedPostedCustomRange?.toDomain(),
        finalSale = finalSale
    )

internal fun MarketplaceSearchCriteriaDomain.toData(formatPrice: (BigDecimal?) -> String?): MarketplaceSearchCriteria =
    MarketplaceSearchCriteria(
        categoryCodes = categoryCodes.takeIf { it.isNotEmpty() },
        conditionCodes = conditionCodes.takeIf { it.isNotEmpty() },
        minPrice = formatPrice(minPrice),
        maxPrice = formatPrice(maxPrice),
        datePosted = datePosted,
        datedPostedCustomRange = datedPostedCustomRange?.let { datedPostedCustomRange ->
            ListingDatePostedCustomRange(
                from = datedPostedCustomRange.from.toString(),
                to = datedPostedCustomRange.to.toString()
            )
        },
        finalSale = finalSale
    )