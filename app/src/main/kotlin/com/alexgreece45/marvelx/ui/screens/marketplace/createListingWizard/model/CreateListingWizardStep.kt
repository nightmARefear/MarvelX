package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

enum class CreateListingWizardStep(val order: Int) {
    BASIC_DETAILS(order = 1),
    ADVANCED_DETAILS(order = 2),
    OVERVIEW(order = 3);
}

val CREATE_LISTING_WIZARD_TOTAL_STEPS = CreateListingWizardStep.entries.size