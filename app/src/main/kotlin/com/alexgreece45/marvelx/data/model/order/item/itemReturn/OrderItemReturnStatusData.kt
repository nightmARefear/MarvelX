package com.alexgreece45.marvelx.data.model.order.item.itemReturn

enum class OrderItemReturnStatusData(val key: String) {
    AVAILABLE(key = "available"),
    EXPIRED(key = "expired"),
    FINAL_SALE(key = "final_sale"),
    PENDING(key = "pending"),
    REJECTED(key = "rejected"),
    ACCEPTED(key = "accepted");

    fun isProcessingStatus(): Boolean = this == PENDING || this == ACCEPTED
}