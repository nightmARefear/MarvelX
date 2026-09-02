package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus

enum class OrderItemReturnStatusDomain {
    AVAILABLE,
    EXPIRED,
    FINAL_SALE,
    PENDING,
    REJECTED,
    ITEM_REPLACED,
    REFUNDED;

    fun isProcessingStatus(): Boolean = this == PENDING
            || this == ITEM_REPLACED
            || this == REFUNDED
}

fun OrderItemReturnStatus.toDomain(): OrderItemReturnStatusDomain =
    when (this) {
        OrderItemReturnStatus.AVAILABLE -> OrderItemReturnStatusDomain.AVAILABLE
        OrderItemReturnStatus.EXPIRED -> OrderItemReturnStatusDomain.EXPIRED
        OrderItemReturnStatus.FINAL_SALE -> OrderItemReturnStatusDomain.FINAL_SALE
        OrderItemReturnStatus.PENDING -> OrderItemReturnStatusDomain.PENDING
        OrderItemReturnStatus.REJECTED -> OrderItemReturnStatusDomain.REJECTED
        OrderItemReturnStatus.ITEM_REPLACED -> OrderItemReturnStatusDomain.ITEM_REPLACED
        OrderItemReturnStatus.REFUNDED -> OrderItemReturnStatusDomain.REFUNDED
    }