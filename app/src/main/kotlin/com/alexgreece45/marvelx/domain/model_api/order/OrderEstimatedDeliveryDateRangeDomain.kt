package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import java.time.Instant

data class OrderEstimatedDeliveryDateRangeDomain(
    val from: Instant,
    val to: Instant
)

fun OrderEstimatedDeliveryDateRangeData.toDomain(): OrderEstimatedDeliveryDateRangeDomain =
    OrderEstimatedDeliveryDateRangeDomain(
        from = Instant.parse(from),
        to = Instant.parse(to)
    )
