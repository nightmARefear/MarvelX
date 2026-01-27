package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

data class CreateListingUi(
    val title: CreateListingFieldUi.Text,
    val description: CreateListingFieldUi.Text,

    val condition: CreateListingFieldUi.Enum,
    val price: CreateListingFieldUi.Text
)
