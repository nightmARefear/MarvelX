package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.google.gson.annotations.SerializedName

data class ResponseOrderItemReturn(
    @SerializedName("orderItemReturnId") val orderItemReturnId: String,
    @SerializedName("orderItem") val orderItem: ResponseOrderItem,

    @SerializedName("returnStatus") val returnStatus: String,
    @SerializedName("returnUntilDate") val returnUntilDate: String,
    @SerializedName("refundStatus") val refundStatus: String,
    @SerializedName("returnShippingFee") val returnShippingFee: String? = null,
    @SerializedName("refundAmount") val refundAmount: String? = null,

    @SerializedName("returnReason") val returnReason: String? = null,
    @SerializedName("refundRequested") val refundRequested: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("attachedImageUrls") val attachedImageUrls: List<String>? = null,
    @SerializedName("condition") val condition: String? = null,
    @SerializedName("returnMethod") val returnMethod: String? = null,

    @SerializedName("submittedAt") val submittedAt: String? = null,

    @SerializedName("sellerMessage") val sellerMessage: String? = null,
)