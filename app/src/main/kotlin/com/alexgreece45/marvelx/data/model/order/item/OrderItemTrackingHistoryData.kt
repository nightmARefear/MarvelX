package com.alexgreece45.marvelx.data.model.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItemTrackingHistory
import com.alexgreece45.marvelx.data.model.order.OrderCourierData
import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemStatusData
import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnEligibilityData
import com.alexgreece45.marvelx.data.model.order.item.itemReturn.toData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoHistoryItemData
import com.alexgreece45.marvelx.data.model.order.tracking.toData

data class OrderItemTrackingHistoryData(
    val trackingId: String,
    val courier: OrderCourierData,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeData,
    val status: OrderItemStatusData,
    val shipped: OrderTrackingInfoHistoryItemData?,
    val departedSortingFacility: OrderTrackingInfoHistoryItemData?,
    val outForDelivery: OrderTrackingInfoHistoryItemData?,
    val delivered: OrderTrackingInfoHistoryItemData?,
    val returnEligibility: OrderItemReturnEligibilityData
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
        delivered = delivered?.toData(),
        returnEligibility = returnEligibility.toData()
    )