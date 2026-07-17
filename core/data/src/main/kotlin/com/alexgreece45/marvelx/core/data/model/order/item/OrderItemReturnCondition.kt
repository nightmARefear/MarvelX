package com.alexgreece45.marvelx.core.data.model.order.item

enum class OrderItemReturnCondition(val key: String) {
    ORIGINAL_PACKAGING("original_packaging"),
    PACKAGE_OPENED("package_opened"),
    LIGHTLY_USED("lightly_used");
}