package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderEstimatedDeliveryDateRange

data class OrderEstimatedDeliveryDateRangeData(
    val from: String,
    val to: String
)

fun ResponseOrderEstimatedDeliveryDateRange.toData(): OrderEstimatedDeliveryDateRangeData =
    OrderEstimatedDeliveryDateRangeData(
        from = from,
        to = to
    )
