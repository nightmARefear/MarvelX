package com.alexgreece45.marvelx.ui.screens.orders.model.item

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.domain.model_api.order.OrderId
import com.alexgreece45.marvelx.domain.model_api.order.item.OrderItemDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.util.extensions.formatPrice

@Immutable
data class OrderItemUi(
    val id: OrderId,
    val sellerUserId: String,
    val title: String,
    val coverImageUrl: String,
    val status: OrderItemStatusUi,
    val price: BigDecimalUi
)

fun OrderItemDomain.toUi(priceScale: Int): OrderItemUi =
    OrderItemUi(
        id = id,
        sellerUserId = sellerUserId,
        title = title,
        coverImageUrl = coverImageUrl,
        status = status.toUi(),
        price = BigDecimalUi(
            value = price,
            label = price.formatPrice(scale = priceScale)
        )
    )
