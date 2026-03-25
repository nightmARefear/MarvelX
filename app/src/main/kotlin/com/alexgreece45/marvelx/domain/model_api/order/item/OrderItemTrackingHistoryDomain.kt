package com.alexgreece45.marvelx.domain.model_api.order.item

import com.alexgreece45.marvelx.data.model.order.item.OrderItemTrackingHistoryData
import com.alexgreece45.marvelx.domain.model_api.order.OrderCourierDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingHistoryItemDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.toDomain

data class OrderItemTrackingHistoryDomain(
    val trackingId: String,
    val courier: OrderCourierDomain,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeDomain,
    val status: OrderItemStatusDomain,
    val shipped: OrderTrackingHistoryItemDomain? = null,
    val departedSortingFacility: OrderTrackingHistoryItemDomain? = null,
    val outForDelivery: OrderTrackingHistoryItemDomain? = null,
    val delivered: OrderTrackingHistoryItemDomain? = null
)

fun OrderItemTrackingHistoryData.toDomain(): OrderItemTrackingHistoryDomain =
    OrderItemTrackingHistoryDomain(
        trackingId = trackingId,
        courier = courier.toDomain(),
        estimatedDelivery = estimatedDelivery.toDomain(),
        status = status.toDomain(),
        shipped = shipped?.toDomain(),
        departedSortingFacility = departedSortingFacility?.toDomain(),
        outForDelivery = outForDelivery?.toDomain(),
        delivered = delivered?.toDomain()
    )
