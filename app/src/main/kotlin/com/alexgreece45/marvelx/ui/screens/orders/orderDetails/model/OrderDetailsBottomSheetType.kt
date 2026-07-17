package com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model

import com.alexgreece45.marvelx.ui.screens.orders.model.OrderStatusUi

sealed interface OrderDetailsBottomSheetType {
    data class OrderStatusInfo(val orderStatusUi: OrderStatusUi) : OrderDetailsBottomSheetType
}
