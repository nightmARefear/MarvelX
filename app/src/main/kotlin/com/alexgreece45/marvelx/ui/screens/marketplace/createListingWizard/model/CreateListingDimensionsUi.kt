package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

data class CreateListingDimensionsUi(
    val width: WizardInputFieldUi.Integer,
    val height: WizardInputFieldUi.Integer,
) {

    val isValid: Boolean
        get() = width.isValid && height.isValid
}
