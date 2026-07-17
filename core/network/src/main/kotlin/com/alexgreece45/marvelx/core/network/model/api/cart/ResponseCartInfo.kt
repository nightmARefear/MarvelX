package com.alexgreece45.marvelx.core.network.model.api.cart

import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import com.google.gson.annotations.SerializedName

data class ResponseCartInfo(
    @SerializedName("items") val items: List<ResponseListingPreview>,
    @SerializedName("summary") val summary: ResponseCartSummary
)
