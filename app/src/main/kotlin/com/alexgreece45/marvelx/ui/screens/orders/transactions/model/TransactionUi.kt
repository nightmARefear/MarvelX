package com.alexgreece45.marvelx.ui.screens.orders.transactions.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.toOrderFormatShort
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.order.transaction.TransactionDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.OrderItemReturnStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.toUi

/**
 * A single row of the Transactions screen: one order item that was bought or sold.
 *
 * @param counterpartyUserId The seller for a purchase, the buyer for a sale.
 */
@Immutable
internal data class TransactionUi(
    val orderId: OrderIdDomain,
    val orderItemId: OrderIdDomain,
    val title: String,
    val coverImageUrl: String,
    val price: BigDecimalUi,
    val date: InstantUi,
    val status: OrderItemStatusUi,
    val returnStatus: OrderItemReturnStatusUi?,
    val refundAmount: BigDecimalUi?,
    val counterpartyUserId: String
)

internal fun TransactionDomain.toUi(priceScale: Int): TransactionUi =
    TransactionUi(
        orderId = orderId,
        orderItemId = orderItemId,
        title = title,
        coverImageUrl = coverImageUrl,
        price = BigDecimalUi(
            value = price,
            label = price.formatPrice(scale = priceScale)
        ),
        date = InstantUi(
            value = date,
            label = date.toOrderFormatShort()
        ),
        status = status.toUi(),
        returnStatus = returnStatus?.toUi(),
        refundAmount = refundAmount?.let { amount ->
            BigDecimalUi(
                value = amount,
                label = amount.formatPrice(scale = priceScale)
            )
        },
        counterpartyUserId = counterpartyUserId
    )
