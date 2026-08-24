package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnMethod

@Immutable
internal enum class OrderItemReturnMethodUi(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    PREPAID_RETURN_LABEL(
        titleResId = R.string.order_item_return_method_prepaid_return_label_title,
        descriptionResId = R.string.order_item_return_method_prepaid_return_label_description
    ),
    REQUEST_PICKUP(
        titleResId = R.string.order_item_return_method_request_pickup_title,
        descriptionResId = R.string.order_item_return_method_request_pickup_description
    );
}

internal fun OrderItemReturnMethod.toUi(): OrderItemReturnMethodUi =
    when (this) {
        OrderItemReturnMethod.PREPAID_RETURN_LABEL -> OrderItemReturnMethodUi.PREPAID_RETURN_LABEL
        OrderItemReturnMethod.REQUEST_COURIER_PICKUP -> OrderItemReturnMethodUi.REQUEST_PICKUP
    }

internal fun OrderItemReturnMethodUi.toData(): OrderItemReturnMethod =
    when (this) {
        OrderItemReturnMethodUi.PREPAID_RETURN_LABEL -> OrderItemReturnMethod.PREPAID_RETURN_LABEL
        OrderItemReturnMethodUi.REQUEST_PICKUP -> OrderItemReturnMethod.REQUEST_COURIER_PICKUP
    }