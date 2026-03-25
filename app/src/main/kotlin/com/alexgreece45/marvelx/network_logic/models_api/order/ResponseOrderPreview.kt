package com.alexgreece45.marvelx.network_logic.models_api.order

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderEstimatedDeliveryDateRange
import com.google.gson.annotations.SerializedName

data class ResponseOrderPreview(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("coverImageUrls") val coverImageUrls: List<String>,
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("estimatedDeliveryDate") val estimatedDeliveryDate: ResponseOrderEstimatedDeliveryDateRange,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: String,
)
