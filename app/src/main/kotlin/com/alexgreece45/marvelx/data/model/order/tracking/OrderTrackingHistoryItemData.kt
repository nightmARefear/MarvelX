package com.alexgreece45.marvelx.data.model.order.tracking

import com.alexgreece45.marvelx.network_logic.models_api.order.tracking.ResponseOrderTrackingHistoryItem

data class OrderTrackingInfoHistoryItemData(
    val location: String,
    val date: String
)

fun ResponseOrderTrackingHistoryItem.toData(): OrderTrackingInfoHistoryItemData =
    OrderTrackingInfoHistoryItemData(
        location = location,
        date = date
    )
