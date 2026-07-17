package com.alexgreece45.marvelx.core.network.model

internal enum class OrderStatus(val code: String) {
    Processing(code = "processing"),
    PartiallyShipped(code = "partially_shipped"),
    Shipped(code = "shipped"),
    PartiallyDelivered(code = "partially_delivered"),
    Delivered(code = "delivered");
}