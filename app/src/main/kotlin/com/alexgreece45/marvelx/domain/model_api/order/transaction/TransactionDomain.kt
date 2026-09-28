package com.alexgreece45.marvelx.domain.model_api.order.transaction

import com.alexgreece45.marvelx.core.data.model.order.transaction.TransactionData
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.toDomain
import com.alexgreece45.marvelx.domain.model_api.order.toDomain
import java.math.BigDecimal
import java.time.Instant

/**
 * A single order item in the current user's purchase or sale history.
 *
 * @param counterpartyUserId The seller for a purchase, the buyer for a sale.
 */
data class TransactionDomain(
    val orderId: OrderIdDomain,
    val orderItemId: OrderIdDomain,
    val listingId: String,
    val title: String,
    val coverImageUrl: String,
    val price: BigDecimal,
    val date: Instant,
    val status: OrderItemStatusDomain,
    val returnStatus: OrderItemReturnStatusDomain?,
    val refundAmount: BigDecimal?,
    val counterpartyUserId: String
)

fun TransactionData.toDomain(): TransactionDomain =
    TransactionDomain(
        orderId = OrderIdDomain(orderId),
        orderItemId = OrderIdDomain(orderItemId),
        listingId = listingId,
        title = title,
        coverImageUrl = coverImageUrl,
        price = price.toBigDecimal(),
        date = Instant.parse(date),
        status = status.toDomain(),
        returnStatus = returnStatus?.toDomain(),
        refundAmount = refundAmount?.toBigDecimal(),
        counterpartyUserId = counterpartyUserId
    )
