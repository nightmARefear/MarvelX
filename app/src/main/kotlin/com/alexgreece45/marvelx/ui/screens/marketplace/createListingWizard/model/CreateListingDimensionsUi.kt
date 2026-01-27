package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

data class CreateListingDimensionsUi(
    val width: CreateListingFieldUi.Integer,
    val height: CreateListingFieldUi.Integer,
) {

    val isValid: Boolean
        get() = width.isValid && height.isValid
}
