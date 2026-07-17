package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

// TODO: Review later.
//sealed interface CreateListingWizardMode {
//    data class Create(val createListing: CreateListingUi) : CreateListingWizardMode
//    data class Update(val title: String? = null, val description: String? = null) :
//        CreateListingWizardMode
//}

enum class CreateListingWizardMode(val canEdit: Boolean) {
    CREATE(canEdit = true),
    UPDATE(canEdit = false)
}