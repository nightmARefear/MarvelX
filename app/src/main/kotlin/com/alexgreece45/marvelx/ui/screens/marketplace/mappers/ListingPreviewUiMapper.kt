package com.alexgreece45.marvelx.ui.screens.marketplace.mappers

import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingPreviewUi
import com.alexgreece45.marvelx.util.extensions.formatPrice

fun ListingPreviewDomain.toUi(): ListingPreviewUi =
    ListingPreviewUi(
        id = id,
        title = title,
        description = description,
        priceLabel = price.formatPrice(),
        coverImageUrl = coverImageUrl
    )
