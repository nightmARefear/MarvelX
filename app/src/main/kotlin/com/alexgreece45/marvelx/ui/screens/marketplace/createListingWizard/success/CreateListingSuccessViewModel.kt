package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.createListingWizard.CreateListingWizardController
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import com.alexgreece45.marvelx.util.ResourceProvider

class CreateListingSuccessViewModel(
    private val wizardController: CreateListingWizardController,
    private val resourceProvider: ResourceProvider
) :
    BaseViewModel<CreateListingSuccessContract.Event, CreateListingSuccessContract.State, CreateListingSuccessContract.Effect>() {

    override fun setInitialState(): CreateListingSuccessContract.State =
        CreateListingSuccessContract.State()

    override fun handleEvents(event: CreateListingSuccessContract.Event) {
        when (event) {
            CreateListingSuccessContract.Event.Init -> {
                val listingId = when (val navArgStatus =
                    NavArgHelper.getArgument<String>(NavArg.NewListingId)) {
                    is NavArgStatus.Exists -> navArgStatus.value
                    NavArgStatus.NotExists -> ""
                }

                setState {
                    copy(
                        isLoading = false,
                        successMessage = resourceProvider.getString(
                            if (wizardController.wizardMode == CreateListingWizardMode.CREATE) R.string.screen_create_listing_success_label_success_creation else R.string.screen_create_listing_success_label_success_update
                        ),
                        listingId = listingId
                    )
                }
            }

            CreateListingSuccessContract.Event.OnViewListingClicked -> {
                setEffect {
                    CreateListingSuccessContract.Effect.Navigation.NavigateToListingDetailsScreen(
                        listingId = currentState.listingId
                    )
                }
            }
        }
    }
}
