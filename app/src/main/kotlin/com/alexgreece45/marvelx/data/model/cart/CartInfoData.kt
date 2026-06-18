package com.alexgreece45.marvelx.data.model.cart

import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.toData

data class CartInfoData(
    val items: List<ListingPreviewData>,
    val summary: CartSummaryData
)

fun com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartInfo.toData(): CartInfoData =
    CartInfoData(
        items = items.map { it.toData() },
        summary = summary.toData()
    )
