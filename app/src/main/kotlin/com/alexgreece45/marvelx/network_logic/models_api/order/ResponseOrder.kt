package com.alexgreece45.marvelx.network_logic.models_api.order

import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.network_logic.models_api.order.tracking.ResponseOrderTrackingInfo
import com.google.gson.annotations.SerializedName

data class ResponseOrder(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("status") val status: String,
    @SerializedName("items") val items: List<ResponseOrderItem>,
    @SerializedName("trackingInfo") val trackingInfo: ResponseOrderTrackingInfo,
    @SerializedName("checkoutInfo") val checkoutInfo: ResponseOrderCheckoutInfo,
    @SerializedName("subtotalPrice") val subtotalPrice: String,
    @SerializedName("shippingPrice") val shippingPrice: String,
    @SerializedName("totalPrice") val totalPrice: String,
)
