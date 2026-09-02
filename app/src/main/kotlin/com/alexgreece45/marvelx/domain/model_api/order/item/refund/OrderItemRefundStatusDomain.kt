package com.alexgreece45.marvelx.domain.model_api.order.item.refund

enum class OrderItemRefundStatusDomain {
    AVAILABLE,
    EXPIRED,
    FINAL_SALE,
    PENDING,
    REJECTED,
    ACCEPTED;

    fun isProcessingStatus(): Boolean = this == PENDING || this == ACCEPTED
}