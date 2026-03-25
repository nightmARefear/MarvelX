package com.alexgreece45.marvelx.network_logic.models_api.order.tracking

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderEstimatedDeliveryDateRange
import com.google.gson.annotations.SerializedName

data class ResponseOrderTrackingInfo(
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("estimatedDeliveryDate") val estimatedDeliveryDate: ResponseOrderEstimatedDeliveryDateRange,
)
