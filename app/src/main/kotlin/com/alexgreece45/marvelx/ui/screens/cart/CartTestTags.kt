package com.alexgreece45.marvelx.ui.screens.cart

internal object CartTestTags {
    // CartContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "cart_error_retry_button"

    // CartContract.Event.OnEmptyCartViewButtonClicked
    const val EMPTY_CART_BUTTON = "cart_empty_cart_button"

    // CartContract.Event.OnCartSummaryErrorButtonClicked
    const val SUMMARY_ERROR_RETRY_BUTTON = "cart_summary_error_retry_button"

    // CartContract.Event.OnCartSummaryButtonClicked (`Proceed` / `Pay`)
    const val SUMMARY_BUTTON = "cart_summary_button"

    // CartContract.Event.CartCheckoutEvents.OnEditShippingDetailsButtonClicked and
    // OnEditPaymentDetailsButtonClicked are tagged by the shared CheckoutInfoCard
    // (`checkout_info_card_edit_shipping_details_button`,
    // `checkout_info_card_edit_payment_details_button`).

    /**
     * CartContract.Event.CartItemEvents.OnClicked; one tag per cart item, keyed by the listing id
     * (e.g. `cart_item_42_card`).
     */
    fun itemCard(listingId: String) = "cart_item_${listingId}_card"

    /**
     * CartContract.Event.CartItemEvents.OnFavoriteButtonClicked; one tag per cart item.
     */
    fun itemFavoriteButton(listingId: String) = "cart_item_${listingId}_favorite_button"

    /**
     * CartContract.Event.CartItemEvents.OnDeleteButtonClicked; one tag per cart item.
     */
    fun itemDeleteButton(listingId: String) = "cart_item_${listingId}_delete_button"
}
