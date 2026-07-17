package com.alexgreece45.marvelx.domain.model_api.order.item

import com.alexgreece45.marvelx.data.model.order.item.OrderItemData
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import java.math.BigDecimal

data class OrderItemDomain(
    val id: OrderIdDomain,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val status: OrderItemStatusDomain,
    val price: BigDecimal
)

fun OrderItemData.toDomain(): OrderItemDomain =
    OrderItemDomain(
        id = OrderIdDomain(id),
        sellerUserId = sellerUserId,
        title = title,
        coverImageUrl = coverImageUrl,
        status = status.toDomain(),
        price = price.toBigDecimal()
    )
