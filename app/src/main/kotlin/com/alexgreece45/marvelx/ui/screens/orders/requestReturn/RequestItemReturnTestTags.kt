package com.alexgreece45.marvelx.ui.screens.orders.requestReturn

internal object RequestItemReturnTestTags {
    // RequestItemReturnContract.Event.OnBack
    const val BACK_BUTTON = "request_item_return_back_button"

    // RequestItemReturnContract.Event.OnClose
    const val CLOSE_BUTTON = "request_item_return_close_button"

    // RequestItemReturnContract.Event.OnStickyBottomButtonClicked (`Next` / `Submit`)
    const val STICKY_BOTTOM_BUTTON = "request_item_return_sticky_bottom_button"

    // RequestItemReturnContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "request_item_return_error_retry_button"

    // RequestItemReturnContract.Event.OnBottomSheetErrorButtonClicked
    const val POLICY_BOTTOM_SHEET_ERROR_RETRY_BUTTON =
        "request_item_return_policy_bottom_sheet_error_retry_button"

    // RequestItemReturnContract.Event.OnAgreeToReturnPolicyCheckboxClicked
    const val AGREE_TO_RETURN_POLICY_CHECKBOX = "request_item_return_agree_to_return_policy_checkbox"

    // RequestItemReturnContract.Event.OnReturnPolicyTextClicked
    const val RETURN_POLICY_TEXT = "request_item_return_return_policy_text"

    // RequestItemReturnContract.Event.OnRequestRefundCheckboxClicked
    const val REQUEST_REFUND_CHECKBOX = "request_item_return_request_refund_checkbox"

    // RequestItemReturnContract.Event.OnReturnReasonOtherTextChanged
    const val RETURN_REASON_OTHER_TEXT_FIELD = "request_item_return_return_reason_other_text_field"

    // RequestItemReturnContract.Event.OnDescriptionChanged
    const val DESCRIPTION_TEXT_FIELD = "request_item_return_description_text_field"

    // RequestItemReturnContract.Event.OnUploadPhotosButtonClicked
    const val UPLOAD_PHOTOS_BUTTON = "request_item_return_upload_photos_button"

    // RequestItemReturnContract.Event.OnSellerResponseMessageChanged
    const val SELLER_RESPONSE_MESSAGE_TEXT_FIELD =
        "request_item_return_seller_response_message_text_field"

    /**
     * RequestItemReturnContract.Event.OnReturnReasonItemSelected; one tag per `OrderItemReturnReasonUi`
     * (e.g. `request_item_return_return_reason_defective_item`).
     */
    fun returnReasonItem(reasonName: String) =
        "request_item_return_return_reason_${reasonName.lowercase()}_item"

    /**
     * RequestItemReturnContract.Event.OnReturnConditionItemSelected; one tag per
     * `OrderItemReturnConditionUi`.
     */
    fun returnConditionItem(conditionName: String) =
        "request_item_return_return_condition_${conditionName.lowercase()}_item"

    /**
     * RequestItemReturnContract.Event.OnReturnMethodItemSelected; one tag per
     * `OrderItemReturnMethodUi`.
     */
    fun returnMethodItem(methodName: String) =
        "request_item_return_return_method_${methodName.lowercase()}_item"

    /**
     * RequestItemReturnContract.Event.OnImageDeleteButtonClicked; one tag per attached image, keyed
     * by its 1-based position.
     */
    fun deleteImageButton(position: Int) = "request_item_return_delete_image_${position}_button"

    /**
     * RequestItemReturnContract.Event.OnSellerResponseActionClicked; one tag per
     * `OrderItemReturnSellerActionUi`.
     */
    fun sellerResponseActionButton(actionName: String) =
        "request_item_return_seller_response_${actionName.lowercase()}_button"
}
