package com.alexgreece45.marvelx.domain.model_api.marketplace

import com.alexgreece45.marvelx.core.data.model.marketplace.ListingDatePostedCustomRange
import java.time.LocalDate

data class ListingDatePostedCustomRangeDomain(
    val from: LocalDate,
    val to: LocalDate
)

internal fun ListingDatePostedCustomRange.toDomain(): ListingDatePostedCustomRangeDomain =
    ListingDatePostedCustomRangeDomain(
        from = LocalDate.parse(from),
        to = LocalDate.parse(to)
    )