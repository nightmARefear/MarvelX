package com.alexgreece45.marvelx.core.network.model.api.marketplace.listing

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseListingOrderIds
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ResponseListing(
    @SerializedName("listingId") val listingId: String,
    @SerializedName("sellerUserId") val sellerUserId: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("subcategoryCode") val subcategoryCode: String,
    @SerializedName("conditionCode") val conditionCode: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("imageUrls") val imageUrls: List<String>,
    @SerializedName("details") val details: JsonElement,
    @SerializedName("details") val finalSale: Boolean,
    @SerializedName("postedAt") val postedAt: String,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("favorite") val favorite: Boolean,
    @SerializedName("cart") val cart: Boolean,
    @SerializedName("orderIds") val orderIds: ResponseListingOrderIds? = null,
)