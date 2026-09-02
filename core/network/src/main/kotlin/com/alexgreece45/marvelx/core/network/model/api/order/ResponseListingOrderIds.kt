package com.alexgreece45.marvelx.core.network.model.api.order

import com.google.gson.annotations.SerializedName

data class ResponseListingOrderIds(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("orderItemId") val orderItemId: String
)