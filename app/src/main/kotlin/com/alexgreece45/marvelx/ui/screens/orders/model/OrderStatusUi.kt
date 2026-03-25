package com.alexgreece45.marvelx.ui.screens.orders.model

import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderStatusDomain
import com.alexgreece45.marvelx.ui.theme.values.customColors

@Immutable
sealed class OrderStatusUi(
    @StringRes val labelResId: Int,
    @StringRes val shortDescriptionResId: Int,
    @StringRes val fullDescriptionResId: Int
) {
    data object Processing : OrderStatusUi(
        labelResId = R.string.order_status_ui_label_processing,
        shortDescriptionResId = R.string.order_status_ui_short_description_processing,
        fullDescriptionResId = R.string.order_status_ui_full_description_processing
    )

    data object PartiallyShipped : OrderStatusUi(
        labelResId = R.string.order_status_ui_label_partially_shipped,
        shortDescriptionResId = R.string.order_status_ui_short_description_partially_shipped,
        fullDescriptionResId = R.string.order_status_ui_full_description_partially_shipped
    )

    data object Shipped : OrderStatusUi(
        labelResId = R.string.order_status_ui_label_shipped,
        shortDescriptionResId = R.string.order_status_ui_short_description_shipped,
        fullDescriptionResId = R.string.order_status_ui_full_description_shipped
    )

    data object PartiallyDelivered : OrderStatusUi(
        labelResId = R.string.order_status_ui_label_partially_delivered,
        shortDescriptionResId = R.string.order_status_ui_short_description_partially_delivered,
        fullDescriptionResId = R.string.order_status_ui_full_description_partially_delivered
    )

    data object Delivered : OrderStatusUi(
        labelResId = R.string.order_status_ui_label_delivered,
        shortDescriptionResId = R.string.order_status_ui_short_description_delivered,
        fullDescriptionResId = R.string.order_status_ui_full_description_delivered
    )

    val icon: ImageVector
        get() = when (this) {
            Processing -> Icons.Filled.HourglassEmpty
            PartiallyShipped -> Icons.Outlined.Inventory2
            Shipped -> Icons.Filled.LocalShipping
            PartiallyDelivered -> Icons.Filled.Inventory
            Delivered -> Icons.Filled.CheckCircle
        }

    val backgroundColor: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Processing -> MaterialTheme.customColors.orderStatusBackgroundColors.processing
            PartiallyShipped -> MaterialTheme.customColors.orderStatusBackgroundColors.partiallyShipped
            Shipped -> MaterialTheme.customColors.orderStatusBackgroundColors.shipped
            PartiallyDelivered -> MaterialTheme.customColors.orderStatusBackgroundColors.partiallyDelivered
            Delivered -> MaterialTheme.customColors.orderStatusBackgroundColors.delivered
        }
}

fun OrderStatusDomain.toUi(): OrderStatusUi =
    when (this) {
        OrderStatusDomain.Processing -> OrderStatusUi.Processing
        OrderStatusDomain.PartiallyShipped -> OrderStatusUi.PartiallyShipped
        OrderStatusDomain.Shipped -> OrderStatusUi.Shipped
        OrderStatusDomain.PartiallyDelivered -> OrderStatusUi.PartiallyDelivered
        OrderStatusDomain.Delivered -> OrderStatusUi.Delivered
    }
