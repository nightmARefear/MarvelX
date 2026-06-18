package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model

import android.net.Uri
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingSubcategory
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
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

        data object OnUploadPhotosButtonClicked : Event

        sealed interface ImageSelectionEvents : Event {

            /**
             * Triggered when images are selected in the `photo picker`.
             */
            data class OnSuccess(val imagesFileContentData: List<AttachedFileContentData>?) :
                ImageSelectionEvents

            /**
             * Triggered when an error occurred in the image selection process.
             */
            data object OnFailure : ImageSelectionEvents

            /**
             * Triggered when no images were selected.
             */
            data object OnNoSelection : ImageSelectionEvents
        }

        sealed interface AttachedImageEvents : Event {
            data class OnDeleteIconButtonClicked(val uri: Uri) : AttachedImageEvents
            data class OnReordered(val from: Int, val to: Int) :
                AttachedImageEvents // TODO: To be reviewed.
        }
    }

    data class State(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = true,
        val wizardMode: CreateListingWizardMode = CreateListingWizardMode.CREATE,

        // Update listing data.
        val imageUrls: List<String> = emptyList(),

        // Create listing data.
        val configurationData: CreateListingBasicDetailsAttachedImagesConfigurationData = CreateListingBasicDetailsAttachedImagesConfigurationData(
            maxImageCount = 0,
            maxImageSize = 0L,
            supportedImageFileExtensions = emptyList()
        ),
        val listingCategories: List<ListingCategory> = emptyList(),
        val listingSubcategories: List<ListingSubcategory> = emptyList(),

        // Input data.
        val title: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val description: WizardInputFieldUi.Text = WizardInputFieldUi.Text(),
        val category: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val subcategory: WizardInputFieldUi.Enum = WizardInputFieldUi.Enum(),
        val attachedImages: List<AttachedFileContentData> = emptyList(),

        val error: CreateListingWizardErrorData? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        data object GoToPreviousStep : Effect
        data object OpenPhotoPicker : Effect
        data class ShowToastMessage(val message: String) : Effect
        data object OnImagesReordered : Effect

        data class SetNextButtonEnabled(val enabled: Boolean) : Effect
    }
}
