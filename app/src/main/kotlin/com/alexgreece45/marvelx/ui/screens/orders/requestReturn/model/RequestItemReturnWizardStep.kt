package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

internal enum class RequestItemReturnWizardStep(val order: Int) {
    RETURN_REASON(order = 1),
    RETURN_DETAILS(order = 2),
    OVERVIEW(order = 3);
}