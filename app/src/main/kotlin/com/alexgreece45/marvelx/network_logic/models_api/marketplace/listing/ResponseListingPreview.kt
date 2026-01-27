package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class ResponseListingPreview(
    @SerializedName("id") val id: String,
    @SerializedName("sellerUserId") val sellerUserId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String
)
