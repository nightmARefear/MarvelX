package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.checkout.ShippingLocationFloorNumberDomain

enum class ShippingLocationFloorNumberUi(@StringRes val labelResId: Int) {
    BASEMENT(labelResId = R.string.shipping_location_floor_number_basement),
    GROUND_FLOOR(labelResId = R.string.shipping_location_floor_number_ground_floor),
    FIRST_FLOOR(labelResId = R.string.shipping_location_floor_number_first_floor),
    SECOND_FLOOR(labelResId = R.string.shipping_location_floor_number_second_floor),
    THIRD_FLOOR(labelResId = R.string.shipping_location_floor_number_third_floor),
    FOURTH_FLOOR(labelResId = R.string.shipping_location_floor_number_fourth_floor),
    FIFTH_FLOOR(labelResId = R.string.shipping_location_floor_number_fifth_floor),
    SIXTH_FLOOR(labelResId = R.string.shipping_location_floor_number_sixth_floor),
    SEVENTH_FLOOR(labelResId = R.string.shipping_location_floor_number_seventh_floor),
    EIGHTH_FLOOR(labelResId = R.string.shipping_location_floor_number_eight_floor),
    NINTH_FLOOR(labelResId = R.string.shipping_location_floor_number_ninth_floor),
    TENTH_FLOOR(labelResId = R.string.shipping_location_floor_number_tenth_floor),
    TENTH_PLUS_FLOOR(labelResId = R.string.shipping_location_floor_number_tenth_plus_floor);
}

fun ShippingLocationFloorNumberDomain.toUi(): ShippingLocationFloorNumberUi =
    when (this) {
        ShippingLocationFloorNumberDomain.BASEMENT -> ShippingLocationFloorNumberUi.BASEMENT
        ShippingLocationFloorNumberDomain.GROUND_FLOOR -> ShippingLocationFloorNumberUi.GROUND_FLOOR
        ShippingLocationFloorNumberDomain.FIRST_FLOOR -> ShippingLocationFloorNumberUi.FIRST_FLOOR
        ShippingLocationFloorNumberDomain.SECOND_FLOOR -> ShippingLocationFloorNumberUi.SECOND_FLOOR
        ShippingLocationFloorNumberDomain.THIRD_FLOOR -> ShippingLocationFloorNumberUi.THIRD_FLOOR
        ShippingLocationFloorNumberDomain.FOURTH_FLOOR -> ShippingLocationFloorNumberUi.FOURTH_FLOOR
        ShippingLocationFloorNumberDomain.FIFTH_FLOOR -> ShippingLocationFloorNumberUi.FIFTH_FLOOR
        ShippingLocationFloorNumberDomain.SIXTH_FLOOR -> ShippingLocationFloorNumberUi.SIXTH_FLOOR
        ShippingLocationFloorNumberDomain.SEVENTH_FLOOR -> ShippingLocationFloorNumberUi.SEVENTH_FLOOR
        ShippingLocationFloorNumberDomain.EIGHTH_FLOOR -> ShippingLocationFloorNumberUi.EIGHTH_FLOOR
        ShippingLocationFloorNumberDomain.NINTH_FLOOR -> ShippingLocationFloorNumberUi.NINTH_FLOOR
        ShippingLocationFloorNumberDomain.TENTH_FLOOR -> ShippingLocationFloorNumberUi.TENTH_FLOOR
        ShippingLocationFloorNumberDomain.TENTH_PLUS_FLOOR -> ShippingLocationFloorNumberUi.TENTH_PLUS_FLOOR
    }

fun ShippingLocationFloorNumberUi.toDomain(): ShippingLocationFloorNumberDomain =
    when (this) {
        ShippingLocationFloorNumberUi.BASEMENT -> ShippingLocationFloorNumberDomain.BASEMENT
        ShippingLocationFloorNumberUi.GROUND_FLOOR -> ShippingLocationFloorNumberDomain.GROUND_FLOOR
        ShippingLocationFloorNumberUi.FIRST_FLOOR -> ShippingLocationFloorNumberDomain.FIRST_FLOOR
        ShippingLocationFloorNumberUi.SECOND_FLOOR -> ShippingLocationFloorNumberDomain.SECOND_FLOOR
        ShippingLocationFloorNumberUi.THIRD_FLOOR -> ShippingLocationFloorNumberDomain.THIRD_FLOOR
        ShippingLocationFloorNumberUi.FOURTH_FLOOR -> ShippingLocationFloorNumberDomain.FOURTH_FLOOR
        ShippingLocationFloorNumberUi.FIFTH_FLOOR -> ShippingLocationFloorNumberDomain.FIFTH_FLOOR
        ShippingLocationFloorNumberUi.SIXTH_FLOOR -> ShippingLocationFloorNumberDomain.SIXTH_FLOOR
        ShippingLocationFloorNumberUi.SEVENTH_FLOOR -> ShippingLocationFloorNumberDomain.SEVENTH_FLOOR
        ShippingLocationFloorNumberUi.EIGHTH_FLOOR -> ShippingLocationFloorNumberDomain.EIGHTH_FLOOR
        ShippingLocationFloorNumberUi.NINTH_FLOOR -> ShippingLocationFloorNumberDomain.NINTH_FLOOR
        ShippingLocationFloorNumberUi.TENTH_FLOOR -> ShippingLocationFloorNumberDomain.TENTH_FLOOR
        ShippingLocationFloorNumberUi.TENTH_PLUS_FLOOR -> ShippingLocationFloorNumberDomain.TENTH_PLUS_FLOOR
    }
