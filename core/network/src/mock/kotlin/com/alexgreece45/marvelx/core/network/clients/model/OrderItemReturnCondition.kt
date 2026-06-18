package com.alexgreece45.marvelx.core.network.clients.model

internal enum class OrderItemReturnCondition(val key: String) {
    ORIGINAL_PACKAGING("original_packaging"),
    PACKAGE_OPENED("package_opened"),
    LIGHTLY_USED("lightly_used");
}