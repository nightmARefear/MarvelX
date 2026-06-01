package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.core.network.model.api.order.ResponseOrder
import com.alexgreece45.marvelx.data.model.order.item.OrderItemData
import com.alexgreece45.marvelx.data.model.order.item.toData
import com.alexgreece45.marvelx.data.model.order.tracking.OrderTrackingInfoData
import com.alexgreece45.marvelx.data.model.order.tracking.toData

data class OrderData(
    val orderId: String,
    val status: OrderStatusData,
    val items: List<OrderItemData>,
    val trackingInfo: OrderTrackingInfoData,
    val orderCheckoutInfoData: OrderCheckoutInfoData,
    val subtotalPrice: String,
    val shippingPrice: String,
    val totalPrice: String,
)

fun ResponseOrder.toData(): OrderData =
    OrderData(
        orderId = orderId,
        status = OrderStatusData.entries.first { it.code == status },
        items = items.map { it.toData() },
        trackingInfo = trackingInfo.toData(),
        orderCheckoutInfoData = checkoutInfo.toData(),
        subtotalPrice = subtotalPrice,
        shippingPrice = shippingPrice,
        totalPrice = totalPrice
    )
