package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

enum class ShippingLocationTypeUi(val code: String, @StringRes val labelResId: Int) {
    HOUSE(code = "house", labelResId = R.string.shipping_location_type_house),
    WORK(code = "work", labelResId = R.string.shipping_location_type_work),
    OTHER(code = "other", labelResId = R.string.shipping_location_type_other);
}
