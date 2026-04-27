package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

import com.alexgreece45.marvelx.data.model.order.item.itemReturn.OrderItemReturnStatusData

enum class OrderItemReturnStatusDomain {
    AVAILABLE,
    EXPIRED,
    FINAL_SALE,
    PENDING,
    REJECTED,
    ACCEPTED;

    fun isProcessingStatus(): Boolean = this == PENDING || this == ACCEPTED
}

fun OrderItemReturnStatusData.toDomain(): OrderItemReturnStatusDomain =
    when (this) {
        OrderItemReturnStatusData.AVAILABLE -> OrderItemReturnStatusDomain.AVAILABLE
        OrderItemReturnStatusData.EXPIRED -> OrderItemReturnStatusDomain.EXPIRED
        OrderItemReturnStatusData.FINAL_SALE -> OrderItemReturnStatusDomain.FINAL_SALE
        OrderItemReturnStatusData.PENDING -> OrderItemReturnStatusDomain.PENDING
        OrderItemReturnStatusData.REJECTED -> OrderItemReturnStatusDomain.REJECTED
        OrderItemReturnStatusData.ACCEPTED -> OrderItemReturnStatusDomain.ACCEPTED
    }
