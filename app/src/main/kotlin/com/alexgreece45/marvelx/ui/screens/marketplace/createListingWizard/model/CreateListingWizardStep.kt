package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

enum class CreateListingWizardStep(val order: Int) {
    BASIC_DETAILS(order = 1),
    ADVANCED_DETAILS(order = 2),
    OVERVIEW(order = 3),
    SUCCESS(order = 4);
}

val CREATE_LISTING_WIZARD_TOTAL_STEPS =
    CreateListingWizardStep.entries.size - 1 // one less step due to the success screen being the "result"
