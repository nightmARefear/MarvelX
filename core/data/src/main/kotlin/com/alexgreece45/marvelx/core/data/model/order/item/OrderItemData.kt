package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem

data class OrderItemData(
    val orderItemId: String,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val status: OrderItemStatusData,
    val price: String
)

fun ResponseOrderItem.toData(): OrderItemData =
    OrderItemData(
        orderItemId = orderItemId,
        sellerUserId = sellerUserId,
        title = title,
        coverImageUrl = coverImageUrl,
        status = OrderItemStatusData.entries.first { it.code == status },
        price = price
    )