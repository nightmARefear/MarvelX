package com.alexgreece45.marvelx.data.model.order

enum class OrderItemStatusData(val code: String) {
    Processing(code = "processing"),
    Shipped(code = "shipped"),
    DepartedSortingFacility(code = "departedSortingFacility"),
    OutForDelivery(code = "outForDelivery"),
    Delivered(code = "delivered");
}
