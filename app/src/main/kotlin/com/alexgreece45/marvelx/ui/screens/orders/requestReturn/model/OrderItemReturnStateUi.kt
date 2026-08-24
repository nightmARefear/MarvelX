package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import com.alexgreece45.marvelx.core.common.extensions.defaultFormat
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnState
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.domain.model_api.order.item.toDomain
import com.alexgreece45.marvelx.ui.model_generic.LocalDateUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import java.time.temporal.ChronoUnit

internal sealed interface OrderItemReturnStateUi {
    data class Available(
        val orderItem: OrderItemUi,
        val returnUntilDate: LocalDateUi,
        val isRequestRefundAvailable: Boolean
    ) : OrderItemReturnStateUi {

        val returnUntilRemainingDays: Long
            get() = ChronoUnit.DAYS.between(
                DateUtil.getCurrentLocalDate(),
                returnUntilDate.value
            )
    }

    data class Existing(val orderItemReturn: OrderItemReturnUi) : OrderItemReturnStateUi
}

internal fun OrderItemReturnState.toUi(priceScale: Int): Result<OrderItemReturnStateUi> =
    runCatching {
        when (this) {
            is OrderItemReturnState.Available -> OrderItemReturnStateUi.Available(
                orderItem = this.orderItem.toDomain().toUi(priceScale),
                returnUntilDate = LocalDateUi(
                    value = returnUntilDate,
                    label = returnUntilDate.defaultFormat()
                ),
                isRequestRefundAvailable = refundStatus == OrderItemRefundStatus.AVAILABLE
            )

            is OrderItemReturnState.Existing -> OrderItemReturnStateUi.Existing(
                orderItemReturn = (this.data.toUi(priceScale)).getOrThrow()
            )
        }
    }