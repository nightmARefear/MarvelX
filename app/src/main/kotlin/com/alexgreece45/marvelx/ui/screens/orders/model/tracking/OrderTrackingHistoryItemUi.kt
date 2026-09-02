package com.alexgreece45.marvelx.ui.screens.orders.model.tracking

import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.core.common.extensions.toOrderFormatFull
import com.alexgreece45.marvelx.domain.model_api.order.tracking.OrderTrackingHistoryItemDomain
import com.alexgreece45.marvelx.ui.model_generic.InstantUi

@Immutable
data class OrderTrackingHistoryItemUi(
    val location: String,
    val date: InstantUi
)

fun OrderTrackingHistoryItemDomain.toUi(): OrderTrackingHistoryItemUi =
    OrderTrackingHistoryItemUi(
        location = location,
        date = InstantUi(
            value = date,
            label = date.toOrderFormatFull()
        )
    )