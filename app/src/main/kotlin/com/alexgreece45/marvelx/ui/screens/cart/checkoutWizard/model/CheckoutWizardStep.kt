package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model

internal enum class CheckoutWizardStep {
    SHIPPING_DETAILS,
    PAYMENT_DETAILS,
    OVERVIEW;

    val previousStep: CheckoutWizardStep
        get() = entries.getOrNull(this.ordinal - 1) ?: CheckoutWizardStep.entries.first()

    val nextStep: CheckoutWizardStep
        get() = entries.getOrNull(this.ordinal + 1) ?: CheckoutWizardStep.entries.last()
}