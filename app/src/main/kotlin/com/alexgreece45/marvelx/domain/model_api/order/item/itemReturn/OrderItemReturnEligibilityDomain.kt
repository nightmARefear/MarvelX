package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain
import java.time.LocalDate

data class OrderItemReturnEligibilityDomain(
    val returnStatus: OrderItemReturnStatusDomain?,
    val returnUntilDate: LocalDate,

    val refundUntilDate: LocalDate,
    val refundStatus: OrderItemRefundStatusDomain?
)