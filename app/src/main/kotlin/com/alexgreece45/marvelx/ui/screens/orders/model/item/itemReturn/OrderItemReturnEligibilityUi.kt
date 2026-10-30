package com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn

import com.alexgreece45.marvelx.core.common.extensions.defaultFormat
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnEligibilityDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.refund.OrderItemRefundStatusDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi

data class OrderItemReturnEligibilityUi(
    val returnStatus: OrderItemReturnStatusDomain?,
    val returnUntilDate: InstantUi,

    val refundStatus: OrderItemRefundStatusDomain?,
    val refundUntilDate: InstantUi,

    val refundRequested: Boolean?
) {
    val isRequestRefundAvailable: Boolean get() = refundStatus == OrderItemRefundStatusDomain.AVAILABLE
}

fun OrderItemReturnEligibilityDomain.toUi(): OrderItemReturnEligibilityUi =
    OrderItemReturnEligibilityUi(
        returnStatus = returnStatus,
        returnUntilDate = InstantUi(
            value = returnUntilDate,
            label = returnUntilDate.defaultFormat()
        ),
        refundStatus = refundStatus,
        refundUntilDate = InstantUi(
            value = refundUntilDate,
            label = refundUntilDate.defaultFormat()
        ),
        refundRequested = refundRequested
    )