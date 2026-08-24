package com.alexgreece45.marvelx.core.network.model.order.item

internal enum class OrderItemReturnReason(val key: String) {
    DEFECTIVE(key = "defective"),
    NOT_AS_DESCRIBED(key = "not_as_described"),
    WRONG_ITEM(key = "wrong_item"),
    OTHER(key = "other");
}