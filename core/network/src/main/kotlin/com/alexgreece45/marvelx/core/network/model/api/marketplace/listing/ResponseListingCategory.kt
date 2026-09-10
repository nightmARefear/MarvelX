package com.alexgreece45.marvelx.core.network.model.api.marketplace.listing

import com.google.gson.annotations.SerializedName

data class ResponseListingCategory(
    @SerializedName("categoryId") val id: Int,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("label") val label: String,
    @SerializedName("subcategories") val subcategories: List<ResponseListingSubcategory>
)

data class ResponseListingSubcategory(
    @SerializedName("id") val id: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("label") val label: String
)
