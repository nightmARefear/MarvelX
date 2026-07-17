package com.alexgreece45.marvelx.ui.screens.orders.model

import com.alexgreece45.marvelx.domain.model_api.order.OrderDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.OrderTrackingInfoUi
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.toUi
import com.alexgreece45.marvelx.util.extensions.formatPrice

internal data class OrderUi(
    val orderId: OrderIdDomain,
    val status: OrderStatusUi,
    val statusDescription: String,
    val items: List<OrderItemUi>,
    val trackingInfo: OrderTrackingInfoUi,
    val orderCheckoutInfo: OrderCheckoutInfoUi,
    val subtotalPrice: BigDecimalUi,
    val shippingPrice: BigDecimalUi,
    val totalPrice: BigDecimalUi,
)

internal fun OrderDomain.toUi(
    getString: (Int, Array<Any>) -> String,
    priceScale: Int,
    onErrorLog: (String) -> Unit
): OrderUi? =
    try {
        val statusUi = status.toUi()
        val itemsUi = items.map { it.toUi(priceScale) }

        OrderUi(
            orderId = orderId,
            status = statusUi,
            statusDescription = when (statusUi) {
                OrderStatusUi.Processing,
                OrderStatusUi.Shipped,
                OrderStatusUi.Delivered -> getString(statusUi.shortDescriptionResId, emptyArray())

                OrderStatusUi.PartiallyShipped -> {
                    val count = itemsUi.count {
                        it.status in listOf(
                            OrderItemStatusUi.Shipped,
                            OrderItemStatusUi.DepartedSortingFacility,
                            OrderItemStatusUi.OutForDelivery,
                            OrderItemStatusUi.Delivered // TODO: Review chosen values.
                        )
                    }

                    getString(statusUi.shortDescriptionResId, arrayOf(count, itemsUi.size))
                }

                OrderStatusUi.PartiallyDelivered -> {
                    val count = itemsUi.count {
                        it.status == OrderItemStatusUi.Delivered
                    }

                    getString(statusUi.shortDescriptionResId, arrayOf(count, itemsUi.size))
                }
            },
            items = itemsUi,
            trackingInfo = trackingInfo.toUi(),
            orderCheckoutInfo = orderCheckoutInfoData.toUi(),
            subtotalPrice = BigDecimalUi(
                value = subtotalPrice,
                label = subtotalPrice.formatPrice(priceScale)
            ),
            shippingPrice = BigDecimalUi(
                value = shippingPrice,
                label = shippingPrice.formatPrice(priceScale)
            ),
            totalPrice = BigDecimalUi(
                value = totalPrice,
                label = totalPrice.formatPrice(priceScale)
            )
        )
    } catch (e: Exception) {
        onErrorLog(e.localizedMessage.orEmpty())
        null
    }