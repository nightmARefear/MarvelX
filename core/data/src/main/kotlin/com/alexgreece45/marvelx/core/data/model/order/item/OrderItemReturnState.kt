package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Available
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Existing
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn
import java.math.BigDecimal
import java.time.Instant

sealed interface OrderItemReturnState {
    data class Available(
        val orderItem: OrderItemData,
        /** UTC instant; format it in the local zone. */
        val returnUntilDate: Instant,
        val refundStatus: OrderItemRefundStatus,
        val returnShippingFee: BigDecimal,
        /** Decided by the backend: the item's price minus the return shipping fee. */
        val refundAmount: BigDecimal?
    ) : OrderItemReturnState

    data class Existing(val data: OrderItemReturn) : OrderItemReturnState
}

fun ResponseOrderItemReturn.toDataState(): OrderItemReturnState {
    val returnStatus = OrderItemReturnStatus.entries.first { it.key == returnStatus }
    return when (returnStatus) {
        OrderItemReturnStatus.AVAILABLE -> Available(
            orderItem = this.orderItem.toData(),
            returnUntilDate = Instant.parse(returnUntilDate),
            refundStatus = OrderItemRefundStatus.entries.first { it.key == refundStatus },
            returnShippingFee = returnShippingFee?.toBigDecimal() ?: BigDecimal.ZERO,
            refundAmount = refundAmount?.toBigDecimal()
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