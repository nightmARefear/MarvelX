package com.alexgreece45.marvelx.data.model.order

import com.alexgreece45.marvelx.network_logic.models_api.order.ResponseOrderPreview

data class OrderPreviewData(
    val orderId: String,
    val coverImageUrls: List<String>,
    val orderDate: String,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeData,
    val status: OrderStatusData,
    val price: String
)

fun ResponseOrderPreview.toData(): OrderPreviewData =
    OrderPreviewData(
        orderId = orderId,
        coverImageUrls = coverImageUrls,
        orderDate = orderDate,
        estimatedDeliveryDate = estimatedDeliveryDate.toData(),
        status = OrderStatusData.entries.first { it.code == status },
        price = price
    )
