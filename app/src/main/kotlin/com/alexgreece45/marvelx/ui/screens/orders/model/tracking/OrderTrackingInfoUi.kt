package com.alexgreece45.marvelx.ui.screens.orders.model.tracking

import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingInfoDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.orders.model.OrderEstimatedDeliveryDateRangeUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toUi
import com.alexgreece45.marvelx.util.extensions.toOrderFormatFull

data class OrderTrackingInfoUi(
    val orderDate: InstantUi,
    val estimatedDeliveryDate: OrderEstimatedDeliveryDateRangeUi
)

fun OrderTrackingInfoDomain.toUi(): OrderTrackingInfoUi =
    OrderTrackingInfoUi(
        orderDate = InstantUi(
            value = orderDate,
            label = orderDate.toOrderFormatFull()
        ),
        estimatedDeliveryDate = estimatedDeliveryDate.toUi()
    )
