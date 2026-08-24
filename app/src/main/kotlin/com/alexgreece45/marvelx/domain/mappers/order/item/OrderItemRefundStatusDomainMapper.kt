package com.alexgreece45.marvelx.domain.mappers.order.item

import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain

fun OrderItemRefundStatus.toDomain(): OrderItemRefundStatusDomain =
    when (this) {
        OrderItemRefundStatus.AVAILABLE -> OrderItemRefundStatusDomain.AVAILABLE
        OrderItemRefundStatus.EXPIRED -> OrderItemRefundStatusDomain.EXPIRED
        OrderItemRefundStatus.FINAL_SALE -> OrderItemRefundStatusDomain.FINAL_SALE
        OrderItemRefundStatus.PENDING -> OrderItemRefundStatusDomain.PENDING
        OrderItemRefundStatus.REJECTED -> OrderItemRefundStatusDomain.REJECTED
        OrderItemRefundStatus.ACCEPTED -> OrderItemRefundStatusDomain.ACCEPTED
    }