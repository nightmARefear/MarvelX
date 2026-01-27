package com.alexgreece45.marvelx.domain.model_api.marketplace.listing

import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi

/**
 * The dimensions of a listing in centimeters (cm).
 */
data class ListingDimensions(
    val width: CreateListingFieldUi.Integer = CreateListingFieldUi.Integer(),
    val height: CreateListingFieldUi.Integer = CreateListingFieldUi.Integer(),
) {

    val isValid: Boolean get() = width.isValid && height.isValid
}
