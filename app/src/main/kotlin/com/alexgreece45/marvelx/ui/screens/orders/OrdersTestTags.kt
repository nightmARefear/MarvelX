package com.alexgreece45.marvelx.ui.screens.orders

internal object OrdersTestTags {
    // OrdersContract.Event.OnBackClicked
    const val BACK_BUTTON = "orders_back_button"

    // OrdersContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "orders_error_retry_button"

    // OrdersContract.Event.OnEmptyOrdersButtonClicked
    const val EMPTY_ORDERS_BUTTON = "orders_empty_orders_button"

    // OrdersContract.Event.OrderStatusInfoBottomSheetEvents.OnViewOrderDetailsClicked
    const val VIEW_ORDER_DETAILS_BUTTON = "orders_view_order_details_button"

    /**
     * OrdersContract.Event.OrderEvents.OnClicked; one tag per order, keyed by the order id
     * (e.g. `orders_order_A1001567_card`).
     */
    fun orderCard(orderId: String) = "orders_order_${orderId}_card"

    /**
     * OrdersContract.Event.OrderEvents.OnStatusChipClicked; one tag per order, keyed by the order id.
     */
    fun orderStatusChip(orderId: String) = "orders_order_${orderId}_status_chip"
}
