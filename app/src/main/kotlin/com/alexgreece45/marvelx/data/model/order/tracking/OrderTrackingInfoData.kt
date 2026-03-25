package com.alexgreece45.marvelx.data.model.order.tracking

import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import com.alexgreece45.marvelx.data.model.order.toData
import com.alexgreece45.marvelx.network_logic.models_api.order.tracking.ResponseOrderTrackingInfo

data class OrderTrackingInfoData(
    val orderDate: String,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeData,
)

fun ResponseOrderTrackingInfo.toData(): OrderTrackingInfoData =
    OrderTrackingInfoData(
        orderDate = orderDate,
        estimatedDeliveryDate = estimatedDeliveryDate.toData()
    )
