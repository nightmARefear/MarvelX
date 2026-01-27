package com.alexgreece45.marvelx.network_logic.models_api.marketplace

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import com.google.gson.annotations.SerializedName

data class ResponseMarketplaceCategorySection(
    @SerializedName("title") val title: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("listings") val listings: List<ResponseListingPreview>
)
