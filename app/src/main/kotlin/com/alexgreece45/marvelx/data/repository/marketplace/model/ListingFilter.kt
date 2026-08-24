package com.alexgreece45.marvelx.data.repository.marketplace.model

import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingCategoryCode
import java.math.BigDecimal
import java.time.LocalDate

sealed interface ListingFilter {
    data class Category(val categoryCode: ListingCategoryCode) : ListingFilter
    data class PriceRange(val min: BigDecimal?, val max: BigDecimal?) : ListingFilter
    data class DatePosted(val range: DateRangeOption) : ListingFilter
    data class FinalSale(val isFinalSale: Boolean) : ListingFilter
}

sealed interface DateRangeOption {
    data object Last24Hours : DateRangeOption
    data object Last7Days : DateRangeOption
    data object Last30Days : DateRangeOption
    data class Custom(val startDate: LocalDate, val endDate: LocalDate) : DateRangeOption
}