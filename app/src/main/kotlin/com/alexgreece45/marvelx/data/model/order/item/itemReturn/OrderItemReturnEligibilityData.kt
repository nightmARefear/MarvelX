package com.alexgreece45.marvelx.data.model.order.item.itemReturn

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnEligibility
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import java.time.LocalDate

data class OrderItemReturnEligibilityData(
    val returnStatus: OrderItemReturnStatus?,
    val returnUntilDate: LocalDate,

    val refundUntilDate: LocalDate,
    val refundStatus: OrderItemRefundStatus?
)

fun ResponseOrderItemReturnEligibility.toData(): OrderItemReturnEligibilityData =
    OrderItemReturnEligibilityData(
        returnStatus = returnStatus?.let { key -> OrderItemReturnStatus.entries.first { it.key == key } },
        returnUntilDate = LocalDate.parse(returnUntilDate),
        refundUntilDate = LocalDate.parse(refundUntilDate),
        // Read from refundStatus itself: looking it up with the returnStatus key threw for "refunded" /
        // "item_replaced", which broke Tracking History for every approved return.
        refundStatus = refundStatus?.let { key -> OrderItemRefundStatus.entries.first { it.key == key } }
    )