package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnCondition

@Immutable
internal enum class OrderItemReturnConditionUi(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    ORIGINAL_PACKAGING(
        titleResId = R.string.order_item_return_condition_original_packaging_title,
        descriptionResId = R.string.order_item_return_condition_original_packaging_description
    ),
    PACKAGE_OPENED(
        titleResId = R.string.order_item_return_condition_package_opened_title,
        descriptionResId = R.string.order_item_return_condition_package_opened_description
    ),
    LIGHTLY_USED(
        titleResId = R.string.order_item_return_condition_lightly_used_title,
        descriptionResId = R.string.order_item_return_condition_lightly_used_description
    );
}

internal fun OrderItemReturnCondition.toUi(): OrderItemReturnConditionUi =
    when (this) {
        OrderItemReturnCondition.ORIGINAL_PACKAGING -> OrderItemReturnConditionUi.ORIGINAL_PACKAGING
        OrderItemReturnCondition.PACKAGE_OPENED -> OrderItemReturnConditionUi.PACKAGE_OPENED
        OrderItemReturnCondition.LIGHTLY_USED -> OrderItemReturnConditionUi.LIGHTLY_USED
    }

internal fun OrderItemReturnConditionUi.toData(): OrderItemReturnCondition =
    when (this) {
        OrderItemReturnConditionUi.ORIGINAL_PACKAGING -> OrderItemReturnCondition.ORIGINAL_PACKAGING
        OrderItemReturnConditionUi.PACKAGE_OPENED -> OrderItemReturnCondition.PACKAGE_OPENED
        OrderItemReturnConditionUi.LIGHTLY_USED -> OrderItemReturnCondition.LIGHTLY_USED
    }