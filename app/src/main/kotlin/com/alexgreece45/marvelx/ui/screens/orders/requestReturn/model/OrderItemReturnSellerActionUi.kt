package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnSellerAction

@Immutable
internal enum class OrderItemReturnSellerActionUi(
    @StringRes val labelResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val confirmDialogTitleResId: Int,
    @StringRes val confirmDialogMessageResId: Int,
    @DrawableRes val iconResId: Int,
    val isDestructive: Boolean
) {
    ACCEPT_REFUND(
        labelResId = R.string.screen_request_item_return_seller_action_accept_refund,
        descriptionResId = R.string.screen_request_item_return_seller_action_accept_refund_description,
        confirmDialogTitleResId = R.string.screen_request_item_return_seller_action_accept_refund_dialog_title,
        confirmDialogMessageResId = R.string.screen_request_item_return_seller_action_accept_refund_dialog_message,
        iconResId = R.drawable.ic_order_item_refund,
        isDestructive = false
    ),
    ACCEPT_REPLACE(
        labelResId = R.string.screen_request_item_return_seller_action_accept_replace,
        descriptionResId = R.string.screen_request_item_return_seller_action_accept_replace_description,
        confirmDialogTitleResId = R.string.screen_request_item_return_seller_action_accept_replace_dialog_title,
        confirmDialogMessageResId = R.string.screen_request_item_return_seller_action_accept_replace_dialog_message,
        iconResId = R.drawable.ic_package,
        isDestructive = false
    ),
    REJECT(
        labelResId = R.string.screen_request_item_return_seller_action_reject,
        descriptionResId = R.string.screen_request_item_return_seller_action_reject_description,
        confirmDialogTitleResId = R.string.screen_request_item_return_seller_action_reject_dialog_title,
        confirmDialogMessageResId = R.string.screen_request_item_return_seller_action_reject_dialog_message,
        iconResId = R.drawable.ic_close,
        isDestructive = true
    );
}

internal fun OrderItemReturnSellerActionUi.toData(): OrderItemReturnSellerAction =
    when (this) {
        OrderItemReturnSellerActionUi.REJECT -> OrderItemReturnSellerAction.REJECT
        OrderItemReturnSellerActionUi.ACCEPT_REPLACE -> OrderItemReturnSellerAction.ACCEPT_REPLACE
        OrderItemReturnSellerActionUi.ACCEPT_REFUND -> OrderItemReturnSellerAction.ACCEPT_REFUND
    }
