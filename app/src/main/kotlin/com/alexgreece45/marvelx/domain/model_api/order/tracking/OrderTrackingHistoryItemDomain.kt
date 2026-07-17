package com.alexgreece45.marvelx.domain.model_api.order.tracking

import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoHistoryItemData
import java.time.Instant

data class OrderTrackingHistoryItemDomain(
    val location: String,
    val date: Instant
)

fun OrderTrackingInfoHistoryItemData.toDomain(): OrderTrackingHistoryItemDomain =
    OrderTrackingHistoryItemDomain(
        location = location,
        date = Instant.parse(date)
    )
