package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.google.gson.annotations.SerializedName

data class ResponseOrderItemReturn(
    @SerializedName("status") val status: String,
    @SerializedName("returnReason") val returnReason: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("attachedImageUrls") val attachedImageUrls: List<String>? = null,
    @SerializedName("condition") val condition: String? = null,
    @SerializedName("sellerMessage") val sellerMessage: String? = null
)