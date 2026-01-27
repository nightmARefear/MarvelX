package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model

import android.net.Uri
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardErrorData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails

sealed interface CreateListingBasicDetailsContract {

    sealed interface Event : BaseEvent {
        data class Init(val updateListingBasicDetails: UpdateListingBasicDetails?) : Event
        data object OnResume : Event
        data object OnBackButtonClicked : Event

        data class OnTitleChanged(val newTitle: String) : Event
        data class OnDescriptionChanged(val newDescription: String) : Event

        sealed interface CategoryDropDownEvents : Event {
            data object OnDismissed : Event
            data object OnClicked : Event
            data class OnItemSelected(val listingCategory: ListingCategory) : Event
        }

        sealed interface SubcategoryDropDownEvents : Event {
            data object OnDismissed : Event
            data object OnClicked : Event
            data class OnItemSelected(val listingSubcategory: ListingSubcategory) : Event
        }

        sealed interface ImageEvents : Event {

            /**
             * Triggered when images are selected in the `photo picker`.
             */
            data class OnSelectionSuccess(val imagesFileContentData: List<AttachedFileContentData>?) :
                ImageEvents

            /**
             * Triggered when no images were selected.
             */
            data object OnNoSelection : ImageEvents

            data class OnDeletionClicked(val uri: Uri) : ImageEvents
            data class OnSetAsCoverClicked(val uri: Uri) : ImageEvents
        }

        data object OnStickyBottomButtonClicked : Event
    }

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = true,
        val wizardMode: CreateListingWizardMode = CreateListingWizardMode.CREATE,

        // Update listing data.
        val imageUrls: List<String> = emptyList(),

        // Create listing data.
        val listingCategories: List<ListingCategory> = emptyList(),
        val listingSubcategories: List<ListingSubcategory> = emptyList(),

        // Input data.
        val title: CreateListingFieldUi.Text = CreateListingFieldUi.Text(),
        val description: CreateListingFieldUi.Text = CreateListingFieldUi.Text(),
        val category: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val subcategory: CreateListingFieldUi.Enum = CreateListingFieldUi.Enum(),
        val attachedFiles: List<AttachedFileContentData> = emptyList(),

        val error: CreateListingWizardErrorData? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        data object GoToPreviousStep : Effect

        data class SetNextButtonEnabled(val enabled: Boolean) : Effect
    }
}
