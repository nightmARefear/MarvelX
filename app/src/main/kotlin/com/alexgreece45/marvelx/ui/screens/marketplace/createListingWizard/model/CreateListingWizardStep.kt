package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import androidx.annotation.IntRange

enum class CreateListingWizardStep(@IntRange(1,10) val order: Int) {
    BASIC_DETAILS(order = 1),
    ADVANCED_DETAILS(order = 2),
    LOGISTICS(order = 3),
    SUCCESS_CREATION(order = 4);
}
