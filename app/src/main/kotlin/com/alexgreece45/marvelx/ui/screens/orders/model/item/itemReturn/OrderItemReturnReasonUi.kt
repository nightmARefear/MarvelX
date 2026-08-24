package com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnReason

@Immutable
internal enum class OrderItemReturnReasonUi(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    DEFECTIVE(
        titleResId = R.string.order_item_return_reason_defective_title,
        descriptionResId = R.string.order_item_return_reason_defective_description
    ),
    NOT_AS_DESCRIBED(
        titleResId = R.string.order_item_return_reason_not_as_described_title,
        descriptionResId = R.string.order_item_return_reason_not_as_described_description
    ),
    WRONG_ITEM(
        titleResId = R.string.order_item_return_reason_wrong_item_title,
        descriptionResId = R.string.order_item_return_reason_wrong_item_description
    ),
    OTHER(
        titleResId = R.string.order_item_return_reason_other_title,
        descriptionResId = R.string.order_item_return_reason_other_description
    )
}

internal fun OrderItemReturnReason.toUi(): OrderItemReturnReasonUi =
    when (this) {
        OrderItemReturnReason.DEFECTIVE -> OrderItemReturnReasonUi.DEFECTIVE
        OrderItemReturnReason.NOT_AS_DESCRIBED -> OrderItemReturnReasonUi.NOT_AS_DESCRIBED
        OrderItemReturnReason.WRONG_ITEM -> OrderItemReturnReasonUi.WRONG_ITEM
        OrderItemReturnReason.OTHER -> OrderItemReturnReasonUi.OTHER
    }

internal fun OrderItemReturnReasonUi.toData(): OrderItemReturnReason =
    when (this) {
        OrderItemReturnReasonUi.DEFECTIVE -> OrderItemReturnReason.DEFECTIVE
        OrderItemReturnReasonUi.NOT_AS_DESCRIBED -> OrderItemReturnReason.NOT_AS_DESCRIBED
        OrderItemReturnReasonUi.WRONG_ITEM -> OrderItemReturnReason.WRONG_ITEM
        OrderItemReturnReasonUi.OTHER -> OrderItemReturnReason.OTHER
    }