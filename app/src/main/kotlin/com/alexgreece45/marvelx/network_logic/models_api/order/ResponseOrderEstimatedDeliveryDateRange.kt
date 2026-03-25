package com.alexgreece45.marvelx.network_logic.models_api.order

import com.google.gson.annotations.SerializedName

data class ResponseOrderEstimatedDeliveryDateRange(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
)
