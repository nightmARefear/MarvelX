package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnId

@JvmInline
value class OrderItemReturnId(val value: String) {
    val formatted: String get() = "#$value"

    override fun toString(): String = value

    companion object {
        val EMPTY = OrderItemReturnId("")
    }
}

fun ResponseOrderItemReturnId.toData(): OrderItemReturnId =
    OrderItemReturnId(this.orderItemReturnId)