package com.alexgreece45.marvelx.data.model.order.tracking

import com.alexgreece45.marvelx.core.network.model.api.order.tracking.ResponseOrderTrackingInfo
import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import com.alexgreece45.marvelx.data.model.order.toData

data class OrderTrackingInfoData(
    val orderDate: String,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeData,
)

fun ResponseOrderTrackingInfo.toData(): OrderTrackingInfoData =
    OrderTrackingInfoData(
        orderDate = orderDate,
        estimatedDeliveryDate = estimatedDeliveryDate.toData()
    )
