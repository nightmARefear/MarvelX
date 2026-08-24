package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Available
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Existing
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import java.time.LocalDate

sealed interface OrderItemReturnState {
    data class Available(
        val orderItem: OrderItemData,
        val returnUntilDate: LocalDate,
        val refundStatus: OrderItemRefundStatus
    ) : OrderItemReturnState

    data class Existing(val data: OrderItemReturn) : OrderItemReturnState
}

fun ResponseOrderItemReturn.toDataState(): OrderItemReturnState {
    val returnStatus = OrderItemReturnStatus.entries.first { it.key == returnStatus }
    return when (returnStatus) {
        OrderItemReturnStatus.AVAILABLE -> Available(
            orderItem = this.orderItem.toData(),
            returnUntilDate = LocalDate.parse(returnUntilDate),
            refundStatus = OrderItemRefundStatus.entries.first { it.key == refundStatus }
        )

        OrderItemReturnStatus.FINAL_SALE, OrderItemReturnStatus.EXPIRED -> throw IllegalStateException(
            "Final sale or expired item return state cannot be handled in this screen."
        )

        OrderItemReturnStatus.PENDING,
        OrderItemReturnStatus.REJECTED,
        OrderItemReturnStatus.ITEM_REPLACED,
        OrderItemReturnStatus.REFUNDED -> Existing(
            this.toData()
        )
    }
}