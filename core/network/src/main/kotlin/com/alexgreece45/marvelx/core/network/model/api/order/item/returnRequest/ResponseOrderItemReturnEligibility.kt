package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.google.gson.annotations.SerializedName

data class ResponseOrderItemReturnEligibility(
    @SerializedName("returnStatus") val returnStatus: String? = null,
    @SerializedName("returnUntilDate") val returnUntilDate: String,

    @SerializedName("refundStatus") val refundStatus: String? = null,
    @SerializedName("refundUntilDate") val refundUntilDate: String
)