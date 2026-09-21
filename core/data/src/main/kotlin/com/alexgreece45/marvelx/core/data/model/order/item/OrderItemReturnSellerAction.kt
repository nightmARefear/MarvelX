package com.alexgreece45.marvelx.core.data.model.order.item

enum class OrderItemReturnSellerAction(val key: String) {
    REJECT(key = "reject"),
    ACCEPT_REPLACE(key = "accept_replace"),
    ACCEPT_REFUND(key = "accept_refund"),
}
