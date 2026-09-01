package com.alexgreece45.marvelx.core.network.model.api.marketplace

import com.google.gson.annotations.SerializedName

data class ResponseFilteredListing(
    @SerializedName("listingId") val listingId: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("conditionCode") val conditionCode: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("postedAt") val postedAt: String,
    @SerializedName("isFinalSale") val isFinalSale: Boolean,
    @SerializedName("favorite") val favorite: Boolean,
    @SerializedName("cart") val cart: Boolean
)