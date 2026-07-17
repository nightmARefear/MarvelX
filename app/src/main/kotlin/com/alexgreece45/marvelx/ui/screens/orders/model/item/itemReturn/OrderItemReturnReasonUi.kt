package com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnReason

internal enum class OrderItemReturnReasonUi(@StringRes val labelResId: Int) {
    DEFECTIVE(R.string.order_item_return_reason_defective),
    NOT_AS_DESCRIBED(R.string.order_item_return_reason_not_as_described),
    WRONG_ITEM(R.string.order_item_return_reason_wrong_item),
    CHANGED_MIND(R.string.order_item_return_reason_changed_mind),
    OTHER(R.string.order_item_return_reason_other)
}

internal fun OrderItemReturnReason.toUi(): OrderItemReturnReasonUi =
    when (this) {
        OrderItemReturnReason.DEFECTIVE -> OrderItemReturnReasonUi.DEFECTIVE
        OrderItemReturnReason.NOT_AS_DESCRIBED -> OrderItemReturnReasonUi.NOT_AS_DESCRIBED
        OrderItemReturnReason.WRONG_ITEM -> OrderItemReturnReasonUi.WRONG_ITEM
        OrderItemReturnReason.CHANGED_MIND -> OrderItemReturnReasonUi.CHANGED_MIND
        OrderItemReturnReason.OTHER -> OrderItemReturnReasonUi.OTHER
    }

internal fun OrderItemReturnReasonUi.toData(): OrderItemReturnReason =
    when (this) {
        OrderItemReturnReasonUi.DEFECTIVE -> OrderItemReturnReason.DEFECTIVE
        OrderItemReturnReasonUi.NOT_AS_DESCRIBED -> OrderItemReturnReason.NOT_AS_DESCRIBED
        OrderItemReturnReasonUi.WRONG_ITEM -> OrderItemReturnReason.WRONG_ITEM
        OrderItemReturnReasonUi.CHANGED_MIND -> OrderItemReturnReason.CHANGED_MIND
        OrderItemReturnReasonUi.OTHER -> OrderItemReturnReason.OTHER
    }