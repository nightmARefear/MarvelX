package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus

internal enum class OrderItemReturnStatusUi(@StringRes val labelResId: Int) {
    AVAILABLE(labelResId = R.string.order_item_return_status_available),
    EXPIRED(labelResId = R.string.order_item_return_status_expired),
    FINAL_SALE(labelResId = R.string.order_item_return_status_final_sale),
    REJECTED(labelResId = R.string.order_item_return_status_rejected),
    PENDING(labelResId = R.string.order_item_return_status_pending),
    ITEM_REPLACED(labelResId = R.string.order_item_return_status_item_replaced),
    REFUNDED(labelResId = R.string.order_item_return_status_refunded);
}

internal fun OrderItemReturnStatus.toUi(): OrderItemReturnStatusUi =
    when (this) {
        OrderItemReturnStatus.AVAILABLE -> OrderItemReturnStatusUi.AVAILABLE
        OrderItemReturnStatus.EXPIRED -> OrderItemReturnStatusUi.EXPIRED
        OrderItemReturnStatus.FINAL_SALE -> OrderItemReturnStatusUi.FINAL_SALE
        OrderItemReturnStatus.REJECTED -> OrderItemReturnStatusUi.REJECTED
        OrderItemReturnStatus.PENDING -> OrderItemReturnStatusUi.PENDING
        OrderItemReturnStatus.ITEM_REPLACED -> OrderItemReturnStatusUi.ITEM_REPLACED
        OrderItemReturnStatus.REFUNDED -> OrderItemReturnStatusUi.REFUNDED
    }