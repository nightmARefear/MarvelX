package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain
import java.time.Instant

data class OrderItemReturnEligibilityDomain(
    val returnStatus: OrderItemReturnStatusDomain?,
    val returnUntilDate: Instant,

    val refundUntilDate: Instant,
    val refundStatus: OrderItemRefundStatusDomain?,
    val refundRequested: Boolean?
)