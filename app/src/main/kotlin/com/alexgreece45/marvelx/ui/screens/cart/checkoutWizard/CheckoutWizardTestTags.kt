package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard

internal object CheckoutWizardTestTags {
    // CheckoutWizardContract.Event.OnCloseButtonClicked (shipping details step)
    const val CLOSE_BUTTON = "checkout_wizard_close_button"

    // CheckoutWizardContract.Event.OnBackButtonClicked (payment details and overview steps)
    const val BACK_BUTTON = "checkout_wizard_back_button"

    // CheckoutWizardContract.Event.OnStickyBottomButtonClicked
    const val NEXT_BUTTON = "checkout_wizard_next_button"

    // CheckoutWizardContract.Event.AlertDialogEvents.* use the shared alert dialog tags
    // (`alert_dialog_confirm_button`, `alert_dialog_dismiss_button`).
}
