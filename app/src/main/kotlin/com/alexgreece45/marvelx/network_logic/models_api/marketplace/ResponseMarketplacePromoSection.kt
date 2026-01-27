package com.alexgreece45.marvelx.network_logic.models_api.marketplace

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import com.google.gson.annotations.SerializedName

data class ResponseMarketplacePromoSection(
    @SerializedName("title") val title: String,
    @SerializedName("listings") val listings: List<ResponseListingPreview>
)
