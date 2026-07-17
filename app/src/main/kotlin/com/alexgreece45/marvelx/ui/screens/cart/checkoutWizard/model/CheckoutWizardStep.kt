package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model

enum class CheckoutWizardStep(val order: Int) {
    SHIPPING_DETAILS(order = 1),
    PAYMENT_DETAILS(order = 2),
    OVERVIEW(order = 3),
    SUCCESS(order = 4);
}

val CHECKOUT_WIZARD_TOTAL_STEPS = CheckoutWizardStep.entries.size - 1
