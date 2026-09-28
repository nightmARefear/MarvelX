package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus
import com.alexgreece45.marvelx.domain.model_api.order.item.itemReturn.OrderItemReturnStatusDomain
import com.alexgreece45.marvelx.ui_theme.theme.values.StatusContainerColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors

internal enum class OrderItemReturnStatusUi(@StringRes val labelResId: Int) {
    AVAILABLE(labelResId = R.string.order_item_return_status_available),
    EXPIRED(labelResId = R.string.order_item_return_status_expired),
    FINAL_SALE(labelResId = R.string.order_item_return_status_final_sale),
    REJECTED(labelResId = R.string.order_item_return_status_rejected),
    PENDING(labelResId = R.string.order_item_return_status_pending),
    ITEM_REPLACED(labelResId = R.string.order_item_return_status_item_replaced),
    REFUNDED(labelResId = R.string.order_item_return_status_refunded);
}

/**
 * True once the seller has responded and the request can no longer change state.
 */
internal val OrderItemReturnStatusUi.isResolved: Boolean
    get() = this == OrderItemReturnStatusUi.REJECTED
            || this == OrderItemReturnStatusUi.ITEM_REPLACED
            || this == OrderItemReturnStatusUi.REFUNDED

internal val OrderItemReturnStatusUi.isApproved: Boolean
    get() = this == OrderItemReturnStatusUi.ITEM_REPLACED
            || this == OrderItemReturnStatusUi.REFUNDED

@get:DrawableRes
internal val OrderItemReturnStatusUi.iconResId: Int
    get() = when (this) {
        OrderItemReturnStatusUi.AVAILABLE -> R.drawable.ic_check
        OrderItemReturnStatusUi.EXPIRED -> R.drawable.ic_alarm
        OrderItemReturnStatusUi.FINAL_SALE -> com.alexgreece45.marvelx.ui_theme.R.drawable.ic_warning
        OrderItemReturnStatusUi.REJECTED -> R.drawable.ic_close
        OrderItemReturnStatusUi.PENDING -> R.drawable.ic_hourglass
        OrderItemReturnStatusUi.ITEM_REPLACED -> R.drawable.ic_package
        OrderItemReturnStatusUi.REFUNDED -> R.drawable.ic_order_item_refund
    }

internal val OrderItemReturnStatusUi.statusColors: StatusContainerColors
    @Composable
    @ReadOnlyComposable
    get() = when (this) {
        OrderItemReturnStatusUi.AVAILABLE -> MaterialTheme.customColors.infoStatusContainerColors
        OrderItemReturnStatusUi.PENDING -> MaterialTheme.customColors.warningStatusContainerColors
        OrderItemReturnStatusUi.EXPIRED,
        OrderItemReturnStatusUi.FINAL_SALE,
        OrderItemReturnStatusUi.REJECTED -> MaterialTheme.customColors.errorStatusContainerColors

        OrderItemReturnStatusUi.ITEM_REPLACED,
        OrderItemReturnStatusUi.REFUNDED -> MaterialTheme.customColors.successStatusContainerColors
    }

/**
 * Headline shown in the status banner. Buyer and seller see the same outcome described from
 * their own side, e.g. a pending request is "waiting for the seller" but "needs your response".
 */
@StringRes
internal fun OrderItemReturnStatusUi.headlineResId(isSeller: Boolean): Int =
    when (this) {
        OrderItemReturnStatusUi.PENDING -> if (isSeller) {
            R.string.return_status_headline_pending_seller
        } else {
            R.string.return_status_headline_pending_buyer
        }

        OrderItemReturnStatusUi.REJECTED -> if (isSeller) {
            R.string.return_status_headline_rejected_seller
        } else {
            R.string.return_status_headline_rejected_buyer
        }

        OrderItemReturnStatusUi.ITEM_REPLACED -> if (isSeller) {
            R.string.return_status_headline_replaced_seller
        } else {
            R.string.return_status_headline_replaced_buyer
        }

        OrderItemReturnStatusUi.REFUNDED -> if (isSeller) {
            R.string.return_status_headline_refunded_seller
        } else {
            R.string.return_status_headline_refunded_buyer
        }

        OrderItemReturnStatusUi.AVAILABLE -> R.string.return_status_headline_available
        OrderItemReturnStatusUi.EXPIRED -> R.string.return_status_headline_expired
        OrderItemReturnStatusUi.FINAL_SALE -> R.string.return_status_headline_final_sale
    }

@StringRes
internal fun OrderItemReturnStatusUi.descriptionResId(isSeller: Boolean): Int =
    when (this) {
        OrderItemReturnStatusUi.PENDING -> if (isSeller) {
            R.string.return_status_description_pending_seller
        } else {
            R.string.return_status_description_pending_buyer
        }

        OrderItemReturnStatusUi.REJECTED -> if (isSeller) {
            R.string.return_status_description_rejected_seller
        } else {
            R.string.return_status_description_rejected_buyer
        }

        OrderItemReturnStatusUi.ITEM_REPLACED -> if (isSeller) {
            R.string.return_status_description_replaced_seller
        } else {
            R.string.return_status_description_replaced_buyer
        }

        OrderItemReturnStatusUi.REFUNDED -> if (isSeller) {
            R.string.return_status_description_refunded_seller
        } else {
            R.string.return_status_description_refunded_buyer
        }

        OrderItemReturnStatusUi.AVAILABLE -> R.string.return_status_description_available
        OrderItemReturnStatusUi.EXPIRED -> R.string.return_status_description_expired
        OrderItemReturnStatusUi.FINAL_SALE -> R.string.return_status_description_final_sale
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

internal fun OrderItemReturnStatusDomain.toUi(): OrderItemReturnStatusUi =
    when (this) {
        OrderItemReturnStatusDomain.AVAILABLE -> OrderItemReturnStatusUi.AVAILABLE
        OrderItemReturnStatusDomain.EXPIRED -> OrderItemReturnStatusUi.EXPIRED
        OrderItemReturnStatusDomain.FINAL_SALE -> OrderItemReturnStatusUi.FINAL_SALE
        OrderItemReturnStatusDomain.REJECTED -> OrderItemReturnStatusUi.REJECTED
        OrderItemReturnStatusDomain.PENDING -> OrderItemReturnStatusUi.PENDING
        OrderItemReturnStatusDomain.ITEM_REPLACED -> OrderItemReturnStatusUi.ITEM_REPLACED
        OrderItemReturnStatusDomain.REFUNDED -> OrderItemReturnStatusUi.REFUNDED
    }
