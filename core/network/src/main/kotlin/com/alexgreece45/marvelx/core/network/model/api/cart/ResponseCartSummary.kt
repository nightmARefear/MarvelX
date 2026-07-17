package com.alexgreece45.marvelx.core.network.model.api.cart

import com.google.gson.annotations.SerializedName

data class ResponseCartSummary(
    @SerializedName("subtotalPrice") val subtotalPrice: String,
    @SerializedName("shippingPrice") val shippingPrice: String,
    @SerializedName("totalPrice") val totalPrice: String
)
