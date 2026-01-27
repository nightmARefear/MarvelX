package com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class ResponseListingCategory(
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("label") val label: String,
    @SerializedName("subcategories") val subcategories: List<ResponseListingSubcategory>
)

data class ResponseListingSubcategory(
    @SerializedName("subcategoryCode") val subcategoryCode: String,
    @SerializedName("label") val label: String
)
