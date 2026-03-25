package com.alexgreece45.marvelx.data.model.cart

import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.network_logic.models_api.cart.ResponseCartInfo

data class CartInfoData(
    val items: List<ListingPreviewData>,
    val summary: CartSummaryData
)

fun ResponseCartInfo.toData(): CartInfoData =
    CartInfoData(
        items = items.map { it.toData() },
        summary = summary.toData()
    )
