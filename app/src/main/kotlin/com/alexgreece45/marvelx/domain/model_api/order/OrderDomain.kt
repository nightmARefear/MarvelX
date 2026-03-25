package com.alexgreece45.marvelx.domain.model_api.order

import com.alexgreece45.marvelx.data.model.order.OrderData
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingInfoDomain
import com.alexgreece45.marvelx.domain.model_api.order.tracking.toDomain
import java.math.BigDecimal

data class OrderDomain(
    val orderId: OrderId,
    val status: OrderStatusDomain,
    val items: List<OrderItemDomain>,
    val trackingInfo: OrderTrackingInfoDomain,
    val orderCheckoutInfoData: OrderCheckoutInfoDomain,
    val subtotalPrice: BigDecimal,
    val shippingPrice: BigDecimal,
    val totalPrice: BigDecimal,
)

fun OrderData.toDomain(): OrderDomain =
    OrderDomain(
        orderId = OrderId(orderId),
        status = status.toDomain(),
        items = items.map { it.toDomain() },
        trackingInfo = trackingInfo.toDomain(),
        orderCheckoutInfoData = orderCheckoutInfoData.toDomain(),
        subtotalPrice = subtotalPrice.toBigDecimal(),
        shippingPrice = shippingPrice.toBigDecimal(),
        totalPrice = totalPrice.toBigDecimal()
    )
