package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnSellerAction

@Immutable
internal enum class OrderItemReturnSellerActionUi(@StringRes val labelResId: Int) {
    REJECT(labelResId = R.string.screen_request_item_return_seller_action_reject),
    ACCEPT_REPLACE(labelResId = R.string.screen_request_item_return_seller_action_accept_replace),
    ACCEPT_REFUND(labelResId = R.string.screen_request_item_return_seller_action_accept_refund);
}

internal fun OrderItemReturnSellerActionUi.toData(): OrderItemReturnSellerAction =
    when (this) {
        OrderItemReturnSellerActionUi.REJECT -> OrderItemReturnSellerAction.REJECT
        OrderItemReturnSellerActionUi.ACCEPT_REPLACE -> OrderItemReturnSellerAction.ACCEPT_REPLACE
        OrderItemReturnSellerActionUi.ACCEPT_REFUND -> OrderItemReturnSellerAction.ACCEPT_REFUND
    }
