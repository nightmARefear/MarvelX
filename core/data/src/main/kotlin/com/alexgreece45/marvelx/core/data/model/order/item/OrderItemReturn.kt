package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturn

data class OrderItemReturn(
    val status: OrderItemReturnStatus,
    val reason: OrderItemReturnReason,
    val description: String?,
    val attachedImageUrls: List<String>,
    val condition: OrderItemReturnCondition,
    val sellerMessage: String?
)

internal fun ResponseOrderItemReturn.toData(): OrderItemReturn =
    OrderItemReturn(
        status = OrderItemReturnStatus.entries.first { it.key == status },
        reason = OrderItemReturnReason.entries.first { it.key == reason },
        description = description,
        attachedImageUrls = attachedImageUrls,
        condition = OrderItemReturnCondition.entries.first { it.key == reason },
        sellerMessage = sellerMessage
    )