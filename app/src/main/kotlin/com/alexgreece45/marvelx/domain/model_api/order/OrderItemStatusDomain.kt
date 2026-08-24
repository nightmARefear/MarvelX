package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemStatusData

enum class OrderItemStatusDomain {
    Processing,
    Shipped,
    DepartedSortingFacility,
    OutForDelivery,
    Delivered;
}

fun OrderItemStatusData.toDomain(): OrderItemStatusDomain =
    when (this) {
        OrderItemStatusData.Processing -> OrderItemStatusDomain.Processing
        OrderItemStatusData.Shipped -> OrderItemStatusDomain.Shipped
        OrderItemStatusData.DepartedSortingFacility -> OrderItemStatusDomain.DepartedSortingFacility
        OrderItemStatusData.OutForDelivery -> OrderItemStatusDomain.OutForDelivery
        OrderItemStatusData.Delivered -> OrderItemStatusDomain.Delivered
    }
