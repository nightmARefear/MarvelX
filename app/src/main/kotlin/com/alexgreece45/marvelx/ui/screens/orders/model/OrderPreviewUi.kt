package com.alexgreece45.marvelx.ui.screens.orders.model

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.domain.model_api.order.OrderPreviewDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.util.extensions.formatPrice
import com.alexgreece45.marvelx.util.extensions.toOrderFormatShort

@Immutable
data class OrderPreviewUi(
    val orderId: String,
    val coverImageUrls: List<String>,
    val orderDate: InstantUi,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeUi,
    val status: OrderStatusUi,
    val price: BigDecimalUi,
)

fun OrderPreviewDomain.toUi(priceScale: Int, onErrorLog: (String) -> Unit): OrderPreviewUi? =
    try {
        OrderPreviewUi(
            orderId = orderId,
            coverImageUrls = coverImageUrls,
            orderDate = InstantUi(
                value = orderDate,
                label = orderDate.toOrderFormatShort()
            ),
            estimatedDeliveryDate = estimatedDeliveryDate.toUi(),
            status = status.toUi(),
            price = BigDecimalUi(
                value = price,
                label = price.formatPrice(scale = priceScale)
            )
        )
    } catch (e: Exception) {
        onErrorLog(e.localizedMessage.orEmpty())
        null
    }
