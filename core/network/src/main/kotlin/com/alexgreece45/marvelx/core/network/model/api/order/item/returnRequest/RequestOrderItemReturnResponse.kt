package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.google.gson.annotations.SerializedName

data class RequestOrderItemReturnResponse(
    @SerializedName("action") val action: String,
    @SerializedName("sellerMessage") val sellerMessage: String? = null
)
