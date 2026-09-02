package com.alexgreece45.marvelx.ui.screens.orders.model

import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toZonedDateTime
import com.alexgreece45.marvelx.domain.model_api.order.OrderEstimatedDeliveryDateRangeDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import java.time.Instant
import java.time.LocalDate

data class OrderEstimatedDeliveryDateRangeUi(
    val from: LocalDateUi,
    val to: LocalDateUi
) {

    val estimatedDateRangeLabel: String get() = from.label + " - " + to.label
}

fun OrderEstimatedDeliveryDateRangeDomain.toUi(): OrderEstimatedDeliveryDateRangeUi =
    OrderEstimatedDeliveryDateRangeUi(
        from = LocalDateUi(
            value = from,
            label = from.toOrderEstimatedDeliveryFormatShort()
        ),
        to = LocalDateUi(
            value = to,
            label = if (to.year > from.year) to.toOrderEstimatedDeliveryFormatFull() else to.toOrderEstimatedDeliveryFormatShort()
        )
    )

/**
 * Converts this to a short Order estimated delivery format.
 */
private fun LocalDate.toOrderEstimatedDeliveryFormatShort(): String = this.toCustomFormat("MMM dd")

/**
 * Converts this to a full Order estimated delivery format.
 */
private fun LocalDate.toOrderEstimatedDeliveryFormatFull(): String =
    this.toCustomFormat("MMM dd, yyyy")
