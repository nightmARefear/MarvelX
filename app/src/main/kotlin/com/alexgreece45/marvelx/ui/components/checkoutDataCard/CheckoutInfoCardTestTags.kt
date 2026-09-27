package com.alexgreece45.marvelx.ui.components.checkoutDataCard

/**
 * Shared tags for [CheckoutInfoCard]. The card is embedded in several screens (cart checkout, order
 * details), so the tags are fixed per role rather than per screen.
 */
internal object CheckoutInfoCardTestTags {
    // `onShippingDetailsEditButtonClicked`
    const val EDIT_SHIPPING_DETAILS_BUTTON = "checkout_info_card_edit_shipping_details_button"

    // `onPaymentDetailsEditButtonClicked`
    const val EDIT_PAYMENT_DETAILS_BUTTON = "checkout_info_card_edit_payment_details_button"
}
