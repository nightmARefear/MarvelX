package com.alexgreece45.marvelx.network_logic.models_api.cart

import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingPreview
import com.google.gson.annotations.SerializedName

data class ResponseCartInfo(
    @SerializedName("items") val items: List<ResponseListingPreview>,
    @SerializedName("summary") val summary: ResponseCartSummary
)
