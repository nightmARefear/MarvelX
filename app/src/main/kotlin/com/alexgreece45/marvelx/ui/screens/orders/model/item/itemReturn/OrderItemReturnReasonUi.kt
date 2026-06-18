package com.alexgreece45.marvelx.ui.screens.orders.model.item.itemReturn

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnReasonDomain

internal enum class OrderItemReturnReasonUi(@StringRes val labelResId: Int) {
    DEFECTIVE(R.string.order_item_return_reason_defective),
    NOT_AS_DESCRIBED(R.string.order_item_return_reason_not_as_described),
    WRONG_ITEM(R.string.order_item_return_reason_wrong_item),
    CHANGED_MIND(R.string.order_item_return_reason_changed_mind),
    OTHER(R.string.order_item_return_reason_other)
}

internal fun OrderItemReturnReasonDomain.toUi(): OrderItemReturnReasonUi =
    when (this) {
        OrderItemReturnReasonDomain.DEFECTIVE -> OrderItemReturnReasonUi.DEFECTIVE
        OrderItemReturnReasonDomain.NOT_AS_DESCRIBED -> OrderItemReturnReasonUi.NOT_AS_DESCRIBED
        OrderItemReturnReasonDomain.WRONG_ITEM -> OrderItemReturnReasonUi.WRONG_ITEM
        OrderItemReturnReasonDomain.CHANGED_MIND -> OrderItemReturnReasonUi.CHANGED_MIND
        OrderItemReturnReasonDomain.OTHER -> OrderItemReturnReasonUi.OTHER
    }

internal fun OrderItemReturnReasonUi.toDomain(): OrderItemReturnReasonDomain =
    when (this) {
        OrderItemReturnReasonUi.DEFECTIVE -> OrderItemReturnReasonDomain.DEFECTIVE
        OrderItemReturnReasonUi.NOT_AS_DESCRIBED -> OrderItemReturnReasonDomain.NOT_AS_DESCRIBED
        OrderItemReturnReasonUi.WRONG_ITEM -> OrderItemReturnReasonDomain.WRONG_ITEM
        OrderItemReturnReasonUi.CHANGED_MIND -> OrderItemReturnReasonDomain.CHANGED_MIND
        OrderItemReturnReasonUi.OTHER -> OrderItemReturnReasonDomain.OTHER
    }