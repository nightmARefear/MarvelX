package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingPreviewData
import java.math.BigDecimal

data class ListingPreviewDomain(
    val id: String,
    val title: String,
    val price: BigDecimal,
    val coverImageUrl: String,
    val favorite: Boolean
)

fun ListingPreviewData.toDomain(): ListingPreviewDomain =
    ListingPreviewDomain(
        id = id,
        title = title,
        price = price.toBigDecimal(),
        coverImageUrl = coverImageUrl,
        favorite = favorite
    )
