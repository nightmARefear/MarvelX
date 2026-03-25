package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class ResponseListingPreview(
    @SerializedName("listingId") val listingId: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("favorite") val favorite: Boolean
)
