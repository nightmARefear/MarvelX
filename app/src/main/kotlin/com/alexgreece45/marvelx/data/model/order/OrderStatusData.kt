package com.alexgreece45.marvelx.data.model.order

enum class OrderStatusData(val code: String) {
    Processing(code = "processing"),
    PartiallyShipped(code = "partially_shipped"),
    Shipped(code = "shipped"),
    PartiallyDelivered(code = "partially_delivered"),
    Delivered(code = "delivered");
}
