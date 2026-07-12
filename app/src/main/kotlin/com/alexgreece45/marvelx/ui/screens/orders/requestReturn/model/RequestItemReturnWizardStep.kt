package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

internal enum class RequestItemReturnWizardStep {
    RETURN_REASON,
    RETURN_DETAILS,
    OVERVIEW;

    val previousStep: RequestItemReturnWizardStep
        get() =
            entries[(this.ordinal - 1).coerceAtLeast(0)]

    val nextStep: RequestItemReturnWizardStep
        get() =
            entries[(this.ordinal + 1).coerceAtMost(entries.lastIndex)]

    companion object {
        val INITIAL_STEP = RETURN_REASON
    }
}