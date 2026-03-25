package com.alexgreece45.marvelx.network_logic.models_api.order

import com.google.gson.annotations.SerializedName

data class ResponseOrderId(
    @SerializedName("orderId") val orderId: String
)
