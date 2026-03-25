package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderPreviewData
import java.math.BigDecimal
import java.time.Instant

data class OrderPreviewDomain(
    val orderId: String,
    val coverImageUrls: List<String>,
    val orderDate: Instant,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeDomain,
    val status: OrderStatusDomain,
    val price: BigDecimal,
)

fun OrderPreviewData.toDomain(): OrderPreviewDomain =
    OrderPreviewDomain(
        orderId = orderId,
        coverImageUrls = coverImageUrls,
        orderDate = Instant.parse(orderDate),
        estimatedDeliveryDate = estimatedDeliveryDate.toDomain(),
        status = status.toDomain(),
        price = price.toBigDecimal()
    )
