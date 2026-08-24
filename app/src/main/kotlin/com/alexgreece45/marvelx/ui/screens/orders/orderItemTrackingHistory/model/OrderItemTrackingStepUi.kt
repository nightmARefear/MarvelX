package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.ui.screens.orders.model.tracking.OrderTrackingHistoryItemUi
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class OrderItemTrackingStepUi(
    @StringRes val titleResId: Int,
    val state: OrderItemTrackingStepState,
    val data: OrderTrackingHistoryItemUi?
)

fun mapToTrackingStepList(
    shipped: OrderTrackingHistoryItemUi?,
    departedSortingFacility: OrderTrackingHistoryItemUi?,
    outForDelivery: OrderTrackingHistoryItemUi?,
    delivered: OrderTrackingHistoryItemUi?
): ImmutableList<OrderItemTrackingStepUi> {

    val chronologicalSteps = listOf(
        Pair(R.string.order_item_status_ui_label_shipped, shipped),
        Pair(
            R.string.order_item_status_ui_label_departed_sorting_facility,
            departedSortingFacility
        ),
        Pair(R.string.order_item_status_ui_label_out_for_delivery, outForDelivery),
        Pair(R.string.order_item_status_ui_label_delivered, delivered)
    )

    val currentIndex = chronologicalSteps.indexOfLast { it.second != null }

    return chronologicalSteps.mapIndexed { index, (titleResId, data) ->
        OrderItemTrackingStepUi(
            titleResId = titleResId,
            state = when {
                index < currentIndex -> OrderItemTrackingStepState.COMPLETED
                index == currentIndex -> OrderItemTrackingStepState.CURRENT
                else -> OrderItemTrackingStepState.NEXT
            },
            data = data
        )
    }.toImmutableListOrEmpty()
}