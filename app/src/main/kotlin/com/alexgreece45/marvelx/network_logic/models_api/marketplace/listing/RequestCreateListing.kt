package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class RequestCreateListing(
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("subcategoryCode") val subcategoryCode: String,
    @SerializedName("conditionCode") val conditionCode: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("imageUrls") val imageUrls: List<String>
)
