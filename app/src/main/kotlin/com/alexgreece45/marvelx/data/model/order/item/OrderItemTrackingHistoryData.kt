package com.alexgreece45.marvelx.data.model.order.item

import com.alexgreece45.marvelx.data.model.order.OrderCourierData
import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import com.alexgreece45.marvelx.data.model.order.OrderItemStatusData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoHistoryItemData
import com.alexgreece45.marvelx.data.model.order.tracking.toData
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory

data class OrderItemTrackingHistoryData(
    val trackingId: String,
    val courier: OrderCourierData,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeData,
    val status: OrderItemStatusData,
    val shipped: OrderTrackingInfoHistoryItemData? = null,
    val departedSortingFacility: OrderTrackingInfoHistoryItemData? = null,
    val outForDelivery: OrderTrackingInfoHistoryItemData? = null,
    val delivered: OrderTrackingInfoHistoryItemData? = null,
)

fun ResponseOrderItemTrackingHistory.toData(): OrderItemTrackingHistoryData =
    OrderItemTrackingHistoryData(
        trackingId = trackingId,
        courier = OrderCourierData.entries.first { it.code == courier },
        estimatedDelivery = estimatedDelivery.toData(),
        status = OrderItemStatusData.entries.first { it.code == status },
        shipped = shipped?.toData(),
        departedSortingFacility = departedSortingFacility?.toData(),
        outForDelivery = outForDelivery?.toData(),
        delivered = delivered?.toData()
    )
