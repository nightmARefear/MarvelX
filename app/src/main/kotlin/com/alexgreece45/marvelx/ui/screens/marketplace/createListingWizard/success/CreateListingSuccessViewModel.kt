package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.listing.ListingId
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract.Effect.Navigation.NavigateToListingDetailsScreenAndPopUpToSuccess
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.CreateListingSuccessRoute
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class CreateListingSuccessViewModel(
    private val resourceProvider: ResourceProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CreateListingSuccessContract.Event, CreateListingSuccessContract.State, CreateListingSuccessContract.Effect>() {

    init {
        val route = savedStateHandle.toRoute<CreateListingSuccessRoute>()

        init(
            wizardMode = route.wizardMode,
            listingId = route.listingId
        )
    }

    override fun setInitialState(): CreateListingSuccessContract.State =
        CreateListingSuccessContract.State()

    override fun handleEvents(event: CreateListingSuccessContract.Event) {
        when (event) {
            CreateListingSuccessContract.Event.OnViewListingClicked -> {
                val wizardMode = currentState.wizardMode ?: return
                setEffect {
                    if (wizardMode == CreateListingWizardMode.CREATE) {
                        NavigateToListingDetailsScreenAndPopUpToSuccess(
                            listingId = currentState.listingId
                        )
                    } else {
                        CreateListingSuccessContract.Effect.Navigation.NavigateAndPopUpToListingDetailsScreen(
                            listingId = currentState.listingId
                        )
                    }
                }
            }

            CreateListingSuccessContract.Event.OnBackClicked -> {
                setEffect {
                    CreateListingSuccessContract.Effect.Navigation.GoBack
                }
            }
        }
    }

    private fun init(wizardMode: String, listingId: String) {
        val wizardMode = CreateListingWizardMode.entries.first { it.name == wizardMode }
        setState {
            copy(
                isLoading = false,
                listingId = ListingId(listingId),
                wizardMode = wizardMode,
                successMessage = resourceProvider.getString(
                    if (wizardMode == CreateListingWizardMode.CREATE) R.string.screen_create_listing_success_label_success_creation else R.string.screen_create_listing_success_label_success_update
                )
            )
        }
    }
}