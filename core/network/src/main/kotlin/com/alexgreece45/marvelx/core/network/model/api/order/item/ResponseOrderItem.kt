package com.alexgreece45.marvelx.core.network.model.api.order.item

import com.google.gson.annotations.SerializedName

data class ResponseOrderItem(
    @SerializedName("orderItemId") val orderItemId: String,
    @SerializedName("sellerUserId") val sellerUserId: String,
    @SerializedName("title") val title: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: String
)