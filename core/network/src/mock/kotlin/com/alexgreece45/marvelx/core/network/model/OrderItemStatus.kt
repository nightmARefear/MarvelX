package com.alexgreece45.marvelx.core.network.model

internal enum class OrderItemStatus(val code: String) {
    Processing(code = "processing"),
    Shipped(code = "shipped"),
    DepartedSortingFacility(code = "departedSortingFacility"),
    OutForDelivery(code = "outForDelivery"),
    Delivered(code = "delivered");
}