package com.alexgreece45.marvelx.core.network.model.order.item

internal enum class OrderItemReturnStatus(val key: String) {
    AVAILABLE(key = "available"),
    EXPIRED(key = "expired"),
    FINAL_SALE(key = "final_sale"),
    PENDING(key = "pending"),
    REJECTED(key = "rejected"),
    ITEM_REPLACED(key = "item_replaced"),
    REFUNDED(key = "refunded");

    fun isProcessingStatus(): Boolean =
        this == PENDING
                || this == ITEM_REPLACED
                || this == REFUNDED
}