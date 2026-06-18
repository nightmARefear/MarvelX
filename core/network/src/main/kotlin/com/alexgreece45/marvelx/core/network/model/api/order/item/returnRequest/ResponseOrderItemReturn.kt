package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.google.gson.annotations.SerializedName

data class ResponseOrderItemReturn(
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("attachedImageUrls") val attachedImageUrls: List<String>,
    @SerializedName("condition") val condition: String,
    @SerializedName("sellerMessage") val sellerMessage: String? = null
)