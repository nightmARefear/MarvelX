package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi

/**
 * The dimensions of a listing in centimeters (cm).
 */
data class ListingDimensions(
    val width: WizardInputFieldUi.Integer = WizardInputFieldUi.Integer(),
    val height: WizardInputFieldUi.Integer = WizardInputFieldUi.Integer(),
) {

    val isValid: Boolean get() = width.isValid && height.isValid
}
