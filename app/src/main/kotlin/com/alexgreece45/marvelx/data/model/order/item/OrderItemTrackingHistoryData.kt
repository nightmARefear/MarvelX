package com.alexgreece45.marvelx.data.model.order.item

import com.alexgreece45.marvelx.data.model.order.OrderCourierData
import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import com.alexgreece45.marvelx.data.model.order.OrderItemStatusData
import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnStatusData
import com.alexgreece45.marvelx.data.model.order.item.refund.OrderItemRefundStatusData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoHistoryItemData
import com.alexgreece45.marvelx.data.model.order.tracking.toData
import com.alexgreece45.marvelx.network_logic.models_api.order.item.ResponseOrderItemTrackingHistory

data class OrderItemTrackingHistoryData(
    val trackingId: String,
    val courier: OrderCourierData,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeData,
    val status: OrderItemStatusData,
    val shipped: OrderTrackingInfoHistoryItemData?,
    val departedSortingFacility: OrderTrackingInfoHistoryItemData?,
    val outForDelivery: OrderTrackingInfoHistoryItemData?,
    val delivered: OrderTrackingInfoHistoryItemData?,
    val returnStatus: OrderItemReturnStatusData?,
    val returnUntilDate: String?,
    val refundStatus: OrderItemRefundStatusData?,
    val refundUntilDate: String?,
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
        returnStatus = OrderItemReturnStatusData.entries.first { it.key == returnStatus },
        returnUntilDate = returnUntilDate,
        refundStatus = OrderItemRefundStatusData.entries.first { it.key == refundStatus },
        refundUntilDate = refundUntilDate
    )
