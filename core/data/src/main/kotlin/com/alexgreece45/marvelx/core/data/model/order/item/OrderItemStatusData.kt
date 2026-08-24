package com.alexgreece45.marvelx.core.data.model.order.item

enum class OrderItemStatusData(val code: String) {
    Processing(code = "processing"),
    Shipped(code = "shipped"),
    DepartedSortingFacility(code = "departedSortingFacility"),
    OutForDelivery(code = "outForDelivery"),
    Delivered(code = "delivered");
}