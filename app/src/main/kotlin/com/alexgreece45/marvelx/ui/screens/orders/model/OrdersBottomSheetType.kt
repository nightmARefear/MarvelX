package com.alexgreece45.marvelx.ui.screens.orders.model

sealed interface OrdersBottomSheetType {

    data class OrderStatusInfo(
        val orderId: String,
        val statusUi: OrderStatusUi
    ) : OrdersBottomSheetType
}
