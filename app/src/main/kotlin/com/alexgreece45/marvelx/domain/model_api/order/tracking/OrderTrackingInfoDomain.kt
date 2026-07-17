package com.alexgreece45.marvelx.domain.model_api.order.tracking

import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoData
import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import java.time.Instant

data class OrderTrackingInfoDomain(
    val orderDate: Instant,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeDomain
)

fun OrderTrackingInfoData.toDomain(): OrderTrackingInfoDomain =
    OrderTrackingInfoDomain(
        orderDate = Instant.parse(orderDate),
        estimatedDeliveryDate = estimatedDeliveryDate.toDomain()
    )
