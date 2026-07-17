package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout

data class CreateListingWizardErrorData(
    val label: String,
    val description: String,
    val errorButtonLayout: ErrorButtonLayout.Single
)
