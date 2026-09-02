package com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain

@Immutable
data class OrderItemDetails(
    val orderId: OrderIdDomain,
    val orderItemId: OrderIdDomain,
    val orderItemTitle: String,
    val orderItemCoverImageUrl: String,
    val orderItemPriceLabel: String
) {
    companion object {
        val EMPTY = OrderItemDetails(
            orderId = OrderIdDomain.EMPTY,
            orderItemId = OrderIdDomain.EMPTY,
            orderItemTitle = "",
            orderItemCoverImageUrl = "",
            orderItemPriceLabel = ""
        )
    }
}