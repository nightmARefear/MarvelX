package com.alexgreece45.marvelx.ui.screens.orders.model

import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.util.extensions.toOrderEstimatedDeliveryFormatFull
import com.alexgreece45.marvelx.util.extensions.toOrderEstimatedDeliveryFormatShort
import com.alexgreece45.marvelx.util.extensions.toZonedDateTime

data class OrderEstimatedDeliveryDateRangeUi(
    val from: InstantUi,
    val to: InstantUi
) {

    val estimatedDateRangeLabel: String get() = from.label + " - " + to.label
}

fun OrderEstimatedDeliveryDateRangeDomain.toUi(): OrderEstimatedDeliveryDateRangeUi =
    OrderEstimatedDeliveryDateRangeUi(
        from = InstantUi(
            value = from,
            label = from.toOrderEstimatedDeliveryFormatShort()
        ),
        to = InstantUi(
            value = to,
            label = if (to.toZonedDateTime().year > from.toZonedDateTime().year) to.toOrderEstimatedDeliveryFormatFull() else to.toOrderEstimatedDeliveryFormatShort()
        )
    )
