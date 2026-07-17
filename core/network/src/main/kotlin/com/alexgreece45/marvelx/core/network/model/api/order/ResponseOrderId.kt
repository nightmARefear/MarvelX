package com.alexgreece45.marvelx.core.network.model.api.order

import com.google.gson.annotations.SerializedName

data class ResponseOrderId(
    @SerializedName("orderId") val orderId: String
)
