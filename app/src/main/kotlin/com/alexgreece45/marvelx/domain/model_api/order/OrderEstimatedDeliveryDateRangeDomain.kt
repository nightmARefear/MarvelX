package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderEstimatedDeliveryDateRangeData
import java.time.LocalDate

data class OrderEstimatedDeliveryDateRangeDomain(
    val from: LocalDate,
    val to: LocalDate
)

fun OrderEstimatedDeliveryDateRangeData.toDomain(): OrderEstimatedDeliveryDateRangeDomain =
    OrderEstimatedDeliveryDateRangeDomain(
        from = LocalDate.parse(from),
        to = LocalDate.parse(to)
    )