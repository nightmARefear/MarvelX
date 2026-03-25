package com.alexgreece45.marvelx.ui.screens.orders.model.item

import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemTrackingHistoryDomain
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderCourierUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderEstimatedDeliveryDateRangeUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.OrderTrackingHistoryItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.toUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingStepUi
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.mapToTrackingStepList

data class OrderItemTrackingHistoryUi(
    val trackingId: String,
    val courier: OrderCourierUi,
    val estimatedDelivery: OrderEstimatedDeliveryDateRangeUi,
    val status: OrderItemStatusUi,
    val steps: List<OrderItemTrackingStepUi>,
    val currentStepItem: OrderTrackingHistoryItemUi?
)

fun OrderItemTrackingHistoryDomain.toUi(onErrorLog: (String) -> Unit): OrderItemTrackingHistoryUi? =
    try {
        val steps = mapToTrackingStepList(
            shipped = shipped?.toUi(),
            departedSortingFacility = departedSortingFacility?.toUi(),
            outForDelivery = outForDelivery?.toUi(),
            delivered = delivered?.toUi()
        )

        OrderItemTrackingHistoryUi(
            trackingId = trackingId,
            courier = courier.toUi(),
            estimatedDelivery = estimatedDelivery.toUi(),
            status = status.toUi(),
            steps = steps,
            currentStepItem = steps.lastOrNull { it.data != null }?.data
        )
    } catch (e: Exception) {
        onErrorLog(e.localizedMessage.orEmpty())
        null
    }
