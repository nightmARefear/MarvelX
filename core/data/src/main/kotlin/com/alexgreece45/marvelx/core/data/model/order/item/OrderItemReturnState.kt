package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Available
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Existing
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState.Unavailable
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn

sealed interface OrderItemReturnState {
    data object Available : OrderItemReturnState
    data class Existing(val data: OrderItemReturn) : OrderItemReturnState
    data class Unavailable(val status: OrderItemReturnStatus) : OrderItemReturnState
}

fun ResponseOrderItemReturn.toDataState(): OrderItemReturnState {
    val returnStatus = OrderItemReturnStatus.entries.first { it.key == status }
    return when (returnStatus) {
        OrderItemReturnStatus.AVAILABLE -> Available
        OrderItemReturnStatus.FINAL_SALE, OrderItemReturnStatus.EXPIRED -> Unavailable(
            returnStatus
        )

        OrderItemReturnStatus.PENDING,
        OrderItemReturnStatus.REJECTED,
        OrderItemReturnStatus.ITEM_REPLACED,
        OrderItemReturnStatus.REFUNDED -> Existing(
            this.toData()
        )
    }
}