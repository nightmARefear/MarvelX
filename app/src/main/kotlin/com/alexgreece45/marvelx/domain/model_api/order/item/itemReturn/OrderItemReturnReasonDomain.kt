package com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn

enum class OrderItemReturnReasonDomain(val key: String) {
    DEFECTIVE(key = "defective"),
    NOT_AS_DESCRIBED(key = "not_as_described"),
    WRONG_ITEM(key = "wrong_item"),
    CHANGED_MIND(key = "changed_mind"),
    OTHER(key = "other");
}
