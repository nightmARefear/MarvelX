package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

sealed class RequestItemReturnDialogType(
    @StringRes val titleResId: Int,
    @StringRes val messageResId: Int,
    @StringRes val confirmButtonLabelResId: Int,
    @StringRes val dismissButtonLabelResId: Int
) {
    data object Refund : RequestItemReturnDialogType(
        titleResId = R.string.screen_request_item_return_refund_dialog_title,
        messageResId = R.string.screen_request_item_return_refund_dialog_error_message,
        confirmButtonLabelResId = R.string.screen_request_item_return_refund_dialog_button_positive,
        dismissButtonLabelResId = R.string.screen_request_item_return_refund_dialog_button_negative
    )

    data object Exit : RequestItemReturnDialogType(
        titleResId = R.string.screen_request_item_return_exit_dialog_title,
        messageResId = R.string.screen_request_item_return_exit_dialog_error_message,
        confirmButtonLabelResId = R.string.screen_request_item_return_exit_dialog_button_positive,
        dismissButtonLabelResId = R.string.screen_request_item_return_exit_dialog_button_negative
    )

    /**
     * Confirmation before a seller response is submitted. The decision is irreversible for the
     * seller, so it is never applied straight from the action button.
     */
    internal data class SellerResponse(val action: OrderItemReturnSellerActionUi) :
        RequestItemReturnDialogType(
            titleResId = action.confirmDialogTitleResId,
            messageResId = action.confirmDialogMessageResId,
            confirmButtonLabelResId = action.labelResId,
            dismissButtonLabelResId = R.string.screen_request_item_return_seller_response_dialog_button_negative
        )
}