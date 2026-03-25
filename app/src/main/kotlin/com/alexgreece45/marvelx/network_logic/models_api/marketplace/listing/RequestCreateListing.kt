package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class RequestCreateListing(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category_id") val categoryCode: String,
    @SerializedName("subcategory_id") val subcategoryCode: String,
    @SerializedName("condition_id") val conditionCode: String,
    @SerializedName("price") val price: String
)
