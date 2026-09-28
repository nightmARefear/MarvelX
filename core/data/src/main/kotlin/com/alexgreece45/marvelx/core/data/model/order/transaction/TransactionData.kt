package com.alexgreece45.marvelx.core.data.model.order.transaction

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemStatusData
import com.alexgreece45.marvelx.core.network.model.api.order.transaction.ResponseTransaction

data class TransactionData(
    val orderId: String,
    val orderItemId: String,
    val listingId: String,
    val title: String,
    val coverImageUrl: String,
    val price: String,
    val date: String,
    val status: OrderItemStatusData,
    val returnStatus: OrderItemReturnStatus?,
    val refundAmount: String?,
    val counterpartyUserId: String
)

fun ResponseTransaction.toData(): TransactionData =
    TransactionData(
        orderId = orderId,
        orderItemId = orderItemId,
        listingId = listingId,
        title = title,
        coverImageUrl = coverImageUrl,
        price = price,
        date = date,
        status = OrderItemStatusData.entries.first { it.code == status },
        returnStatus = returnStatus?.let { key -> OrderItemReturnStatus.entries.first { it.key == key } },
        refundAmount = refundAmount,
        counterpartyUserId = counterpartyUserId
    )
