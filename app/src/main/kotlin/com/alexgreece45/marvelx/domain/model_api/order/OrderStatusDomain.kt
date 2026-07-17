package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderStatusData

enum class OrderStatusDomain {
    Processing,
    PartiallyShipped,
    Shipped,
    PartiallyDelivered,
    Delivered;
}

fun OrderStatusData.toDomain(): OrderStatusDomain =
    when (this) {
        OrderStatusData.Processing -> OrderStatusDomain.Processing
        OrderStatusData.PartiallyShipped -> OrderStatusDomain.PartiallyShipped
        OrderStatusData.Shipped -> OrderStatusDomain.Shipped
        OrderStatusData.PartiallyDelivered -> OrderStatusDomain.PartiallyDelivered
        OrderStatusData.Delivered -> OrderStatusDomain.Delivered
    }
