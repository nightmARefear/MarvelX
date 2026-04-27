package com.alexgreece45.marvelx.domain.model_api.order.item.refund

import com.alexgreece45.marvelx.data.model.order.item.refund.OrderItemRefundStatusData

enum class OrderItemRefundStatusDomain {
    AVAILABLE,
    EXPIRED,
    FINAL_SALE,
    PENDING,
    REJECTED,
    ACCEPTED;

    fun isProcessingStatus(): Boolean = this == PENDING || this == ACCEPTED
}

fun OrderItemRefundStatusData.toDomain(): OrderItemRefundStatusDomain =
    when (this) {
        OrderItemRefundStatusData.AVAILABLE -> OrderItemRefundStatusDomain.AVAILABLE
        OrderItemRefundStatusData.EXPIRED -> OrderItemRefundStatusDomain.EXPIRED
        OrderItemRefundStatusData.FINAL_SALE -> OrderItemRefundStatusDomain.FINAL_SALE
        OrderItemRefundStatusData.PENDING -> OrderItemRefundStatusDomain.PENDING
        OrderItemRefundStatusData.REJECTED -> OrderItemRefundStatusDomain.REJECTED
        OrderItemRefundStatusData.ACCEPTED -> OrderItemRefundStatusDomain.ACCEPTED
    }
