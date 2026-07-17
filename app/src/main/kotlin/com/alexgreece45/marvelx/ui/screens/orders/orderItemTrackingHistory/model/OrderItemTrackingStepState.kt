package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model

import androidx.compose.runtime.Immutable

@Immutable
enum class OrderItemTrackingStepState {
    COMPLETED,
    CURRENT,
    NEXT;
}
