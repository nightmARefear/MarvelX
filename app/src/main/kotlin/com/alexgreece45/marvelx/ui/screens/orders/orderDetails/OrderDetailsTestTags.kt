package com.alexgreece45.marvelx.ui.screens.orders.orderDetails

internal object OrderDetailsTestTags {
    // OrderDetailsContract.Event.OnBack
    const val BACK_BUTTON = "order_details_back_button"

    // OrderDetailsContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "order_details_error_retry_button"

    // OrderDetailsContract.Event.OnOrderStatusBannerClicked
    const val ORDER_STATUS_BANNER = "order_details_order_status_banner"

    /**
     * OrderDetailsContract.Event.OrderItemEvents.OnTrackButtonClicked; one tag per order item,
     * keyed by the order item id (e.g. `order_details_item_B28_track_button`).
     */
    fun trackItemButton(orderItemId: String) = "order_details_item_${orderItemId}_track_button"
}
