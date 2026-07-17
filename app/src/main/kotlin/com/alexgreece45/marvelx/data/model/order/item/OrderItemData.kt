package com.alexgreece45.marvelx.data.model.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseOrderItem
import com.alexgreece45.marvelx.data.model.order.OrderItemStatusData

data class OrderItemData(
    val id: String,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val status: OrderItemStatusData,
    val price: String,
)

fun ResponseOrderItem.toData(): OrderItemData =
    OrderItemData(
        id = id,
        sellerUserId = sellerUserId,
        title = title,
        coverImageUrl = coverImageUrl,
        status = OrderItemStatusData.entries.first { it.code == status },
        price = price
    )
