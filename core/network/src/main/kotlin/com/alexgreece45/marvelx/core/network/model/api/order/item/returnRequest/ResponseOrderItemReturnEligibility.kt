package com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest

import com.google.gson.annotations.SerializedName

data class ResponseOrderItemReturnEligibility(
    @SerializedName("returnStatus") val returnStatus: String? = null,
    @SerializedName("returnUntilDate") val returnUntilDate: String,

    @SerializedName("refundStatus") val refundStatus: String? = null,
    @SerializedName("refundUntilDate") val refundUntilDate: String,

    // Whether the buyer's return request asked for a refund (false = replacement); null when no return was requested.
    @SerializedName("refundRequested") val refundRequested: Boolean? = null
)