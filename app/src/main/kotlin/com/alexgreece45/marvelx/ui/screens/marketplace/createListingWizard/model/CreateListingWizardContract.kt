package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model

import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails

sealed interface CreateListingWizardContract {

    sealed interface Event : BaseEvent {
        data class Init(val listingId: String?) : Event

        data object OnBackButtonClicked : Event
        data object OnCloseButtonClicked : Event
        data object OnStickyBottomButtonClicked : Event

        data class OnAttachedImagesContentRead(val readFileContentsData: List<ReadFileContentData>?) :
            Event

        data class OnShowToastMessage(val message: String) : Event

        data class SetNextButtonEnabled(val enabled: Boolean) : Event

        sealed interface AlertDialogEvents : Event {
            data object OnNegativeButtonClicked : AlertDialogEvents
            data object OnPositiveButtonClicked : AlertDialogEvents
        }

        data object OnExitWizard : Event
        data class OnNavigateToListingDetailsScreen(val listingId: String) : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val toolbarTitle: String = "",

        val wizardMode: CreateListingWizardMode? = null,
        val wizardStep: CreateListingWizardStep = CreateListingWizardStep.BASIC_DETAILS,

        val listingUi: ListingUi? = null,
        val createListingUi: CreateListingUi? = null,

        val error: CreateListingWizardErrorData? = null,
        val isStickyBottomButtonEnabled: Boolean = false,
        val isAlertDialogVisible: Boolean = false,
    ) : BaseState {

        val progress: Float
            get() =
                wizardStep.order.toFloat() / CREATE_LISTING_WIZARD_TOTAL_STEPS.toFloat()
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object ExitWizard : Navigation
            data class NavigateToListingDetailsScreenAndExitWizard(val listingId: String) :
                Navigation
        }

        data class ReadFileContentData(val attachedImages: List<AttachedFileContentData>) : Effect

        data class InitBasicDetailsScreen(val updateListingBasicDetails: UpdateListingBasicDetails?) :
            Effect

        data class ShowToastMessage(val message: String) : Effect
    }
}
