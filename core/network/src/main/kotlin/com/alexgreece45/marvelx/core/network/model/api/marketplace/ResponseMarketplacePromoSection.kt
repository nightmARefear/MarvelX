package com.alexgreece45.marvelx.core.network.model.api.marketplace

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import com.google.gson.annotations.SerializedName

data class ResponseMarketplacePromoSection(
    @SerializedName("title") val title: String,
    @SerializedName("listings") val listings: List<ResponseListingPreview>
)