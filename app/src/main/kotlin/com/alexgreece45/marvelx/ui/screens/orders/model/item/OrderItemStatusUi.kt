package com.alexgreece45.marvelx.ui.screens.orders.model.item

import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderItemStatusDomain
import com.alexgreece45.marvelx.ui.theme.values.customColors

@Immutable
sealed class OrderItemStatusUi(@StringRes val labelResId: Int) {
    data object Processing :
        OrderItemStatusUi(labelResId = R.string.order_item_status_ui_label_processing)

    data object Shipped :
        OrderItemStatusUi(labelResId = R.string.order_item_status_ui_label_shipped)

    data object DepartedSortingFacility :
        OrderItemStatusUi(labelResId = R.string.order_item_status_ui_label_departed_sorting_facility)

    data object OutForDelivery :
        OrderItemStatusUi(labelResId = R.string.order_item_status_ui_label_out_for_delivery)

    data object Delivered : OrderItemStatusUi(labelResId = R.string.order_status_ui_label_delivered)

    val backgroundColor: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Processing -> MaterialTheme.customColors.orderStatusBackgroundColors.processing
            Shipped -> MaterialTheme.customColors.orderStatusBackgroundColors.shipped
            DepartedSortingFacility -> MaterialTheme.customColors.orderStatusBackgroundColors.shipped
            OutForDelivery -> MaterialTheme.customColors.orderStatusBackgroundColors.shipped
            Delivered -> MaterialTheme.customColors.orderStatusBackgroundColors.delivered
        }

    val contentColor: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Processing -> MaterialTheme.customColors.orderStatusContentColors.processing
            Shipped -> MaterialTheme.customColors.orderStatusContentColors.shipped
            DepartedSortingFacility -> MaterialTheme.customColors.orderStatusContentColors.departedSortingFacility
            OutForDelivery -> MaterialTheme.customColors.orderStatusContentColors.outForDelivery
            is Delivered -> MaterialTheme.customColors.orderStatusContentColors.delivered
        }
}

fun OrderItemStatusDomain.toUi(): OrderItemStatusUi =
    when (this) {
        OrderItemStatusDomain.Processing -> OrderItemStatusUi.Processing
        OrderItemStatusDomain.Shipped -> OrderItemStatusUi.Shipped
        OrderItemStatusDomain.DepartedSortingFacility -> OrderItemStatusUi.DepartedSortingFacility
        OrderItemStatusDomain.OutForDelivery -> OrderItemStatusUi.OutForDelivery
        OrderItemStatusDomain.Delivered -> OrderItemStatusUi.Delivered
    }
