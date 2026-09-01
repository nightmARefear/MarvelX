package com.alexgreece45.marvelx.core.network.utils

import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseFilteredListing
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

internal fun ResponseFilteredListing.parsedPrice(): BigDecimal = this.price.toBigDecimal()
internal fun ResponseFilteredListing.parsedPostedAt(): LocalDate =
    Instant.parse(this.postedAt).toLocalDate()