package com.alexgreece45.marvelx.core.network.model.api.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderEstimatedDeliveryDateRange
import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingHistoryItem
import com.google.gson.annotations.SerializedName

data class ResponseOrderItemTrackingHistory(
    @SerializedName("trackingId") val trackingId: String,
    @SerializedName("courier") val courier: String,
    @SerializedName("estimatedDelivery") val estimatedDelivery: ResponseOrderEstimatedDeliveryDateRange,
    @SerializedName("status") val status: String,
    @SerializedName("shipped") val shipped: ResponseOrderTrackingHistoryItem? = null,
    @SerializedName("departedSortingFacility") val departedSortingFacility: ResponseOrderTrackingHistoryItem? = null,
    @SerializedName("outForDelivery") val outForDelivery: ResponseOrderTrackingHistoryItem? = null,
    @SerializedName("delivered") val delivered: ResponseOrderTrackingHistoryItem? = null,
    @SerializedName("returnStatus") val returnStatus: String? = null,
    @SerializedName("returnUntilDate") val returnUntilDate: String? = null,
    @SerializedName("refundStatus") val refundStatus: String? = null,
    @SerializedName("refundUntilDate") val refundUntilDate: String? = null,
)