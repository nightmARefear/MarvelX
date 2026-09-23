package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails

internal object CheckoutShippingDetailsTestTags {
    // CheckoutShippingDetailsContract.Event.OnFirstNameChanged
    const val FIRST_NAME_TEXT_FIELD = "checkout_shipping_details_first_name_text_field"

    // CheckoutShippingDetailsContract.Event.OnLastNameChanged
    const val LAST_NAME_TEXT_FIELD = "checkout_shipping_details_last_name_text_field"

    // CheckoutShippingDetailsContract.Event.OnStreetAddressChanged
    const val STREET_ADDRESS_TEXT_FIELD = "checkout_shipping_details_street_address_text_field"

    // CheckoutShippingDetailsContract.Event.OnCityChanged
    const val CITY_TEXT_FIELD = "checkout_shipping_details_city_text_field"

    // CheckoutShippingDetailsContract.Event.OnZipCodeChanged
    const val ZIP_CODE_TEXT_FIELD = "checkout_shipping_details_zip_code_text_field"

    // CheckoutShippingDetailsContract.Event.FloorNumber.OnClicked / OnDismissed
    const val FLOOR_NUMBER_DROPDOWN = "checkout_shipping_details_floor_number_dropdown"

    // CheckoutShippingDetailsContract.Event.LocationType.OnClicked / OnDismissed
    const val LOCATION_TYPE_DROPDOWN = "checkout_shipping_details_location_type_dropdown"

    /**
     * CheckoutShippingDetailsContract.Event.FloorNumber.OnItemSelected; one tag per option, keyed by
     * `EnumGeneric.code`.
     */
    fun floorNumberOption(code: String) = "checkout_shipping_details_floor_number_${code}_option"

    /**
     * CheckoutShippingDetailsContract.Event.LocationType.OnItemSelected; one tag per option, keyed
     * by `EnumGeneric.code`.
     */
    fun locationTypeOption(code: String) = "checkout_shipping_details_location_type_${code}_option"
}
