package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrderEstimatedDeliveryDateRange

data class OrderEstimatedDeliveryDateRangeData(
    val from: String,
    val to: String
)

fun ResponseOrderEstimatedDeliveryDateRange.toData(): OrderEstimatedDeliveryDateRangeData =
    OrderEstimatedDeliveryDateRangeData(
        from = from,
        to = to
    )
