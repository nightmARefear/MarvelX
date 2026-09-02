package com.alexgreece45.marvelx.core.data.model.order.item.refund

enum class OrderItemRefundStatus(val key: String) {
    AVAILABLE(key = "available"),
    EXPIRED(key = "expired"),
    FINAL_SALE(key = "final_sale"),
    PENDING(key = "pending"),
    REJECTED(key = "rejected"),
    ACCEPTED(key = "accepted");
}