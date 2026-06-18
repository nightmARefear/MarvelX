package com.alexgreece45.marvelx.core.network.clients.model

internal enum class OrderItemCondition(val key: String) {
    BAD("bad"),
    FAIR("fair"),
    GOOD("good"),
    VERY_GOOD("very_good"),
    MINT("mint");
}