package com.alexgreece45.marvelx.core.network.model.api.order.tracking

import com.google.gson.annotations.SerializedName

data class ResponseOrderTrackingHistoryItem(
    @SerializedName("location") val location: String,
    @SerializedName("date") val date: String
)
