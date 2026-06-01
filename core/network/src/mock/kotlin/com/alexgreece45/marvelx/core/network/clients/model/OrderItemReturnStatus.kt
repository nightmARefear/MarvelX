package com.alexgreece45.marvelx.core.network.clients.model

internal enum class OrderItemReturnStatus(val key: String) {
    AVAILABLE(key = "available"),
    EXPIRED(key = "expired"),
    FINAL_SALE(key = "final_sale"),
    PENDING(key = "pending"),
    REJECTED(key = "rejected"),
    ACCEPTED(key = "accepted");

    fun isProcessingStatus(): Boolean = this == PENDING || this == ACCEPTED
}