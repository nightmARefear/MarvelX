package com.alexgreece45.marvelx.ui.screens.orders.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderCourierDomain

enum class OrderCourierUi(@DrawableRes val iconResId: Int, @StringRes val labelResId: Int) {
    Acs(
        iconResId = R.drawable.ic_courier_logo_acs,
        labelResId = R.string.order_courier_ui_label_acs
    ),
    Elta(
        iconResId = R.drawable.ic_courier_logo_elta,
        labelResId = R.string.order_courier_ui_label_elta
    ),
    FedEx(
        iconResId = R.drawable.ic_courier_logo_fedx,
        labelResId = R.string.order_courier_ui_label_fedex
    );
}

fun OrderCourierDomain.toUi(): OrderCourierUi =
    when (this) {
        OrderCourierDomain.Acs -> OrderCourierUi.Acs
        OrderCourierDomain.Elta -> OrderCourierUi.Elta
        OrderCourierDomain.FedEx -> OrderCourierUi.FedEx
    }
