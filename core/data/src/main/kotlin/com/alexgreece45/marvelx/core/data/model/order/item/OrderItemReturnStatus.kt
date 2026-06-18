package com.alexgreece45.marvelx.core.data.model.order.item

enum class OrderItemReturnStatus(val key: String) {
    AVAILABLE(key = "available"),
    EXPIRED(key = "expired"),
    FINAL_SALE(key = "final_sale"),
    REJECTED(key = "rejected"),
    PENDING(key = "pending"),
    ITEM_REPLACED(key = "item_replaced"),
    REFUNDED(key = "refunded");

    fun isProcessingStatus(): Boolean =
        this == PENDING
                || this == ITEM_REPLACED
                || this == REFUNDED
}