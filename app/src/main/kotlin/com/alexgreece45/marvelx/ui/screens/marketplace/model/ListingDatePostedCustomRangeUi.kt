package com.alexgreece45.marvelx.ui.screens.marketplace.model

import com.alexgreece45.marvelx.domain.model_api.marketplace.ListingDatePostedCustomRangeDomain
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi

internal data class ListingDatePostedCustomRangeUi(
    val from: LocalDateUi,
    val to: LocalDateUi,
) {
    val label: String get() = "${from.label} - ${to.label}"
}

internal fun ListingDatePostedCustomRangeUi.toDomain(): ListingDatePostedCustomRangeDomain =
    ListingDatePostedCustomRangeDomain(
        from = from.value,
        to = to.value
    )