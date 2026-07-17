package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.listing.ListingId
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract.Effect.Navigation.NavigateToListingDetailsScreen
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class CreateListingSuccessViewModel(
    private val resourceProvider: ResourceProvider
) : BaseViewModel<CreateListingSuccessContract.Event, CreateListingSuccessContract.State, CreateListingSuccessContract.Effect>() {

    override fun setInitialState(): CreateListingSuccessContract.State =
        CreateListingSuccessContract.State()

    override fun handleEvents(event: CreateListingSuccessContract.Event) {
        when (event) {
            is CreateListingSuccessContract.Event.Init -> {
                setState {
                    copy(
                        isLoading = false,
                        successMessage = resourceProvider.getString(
                            if (event.wizardMode == CreateListingWizardMode.CREATE.name) R.string.screen_create_listing_success_label_success_creation else R.string.screen_create_listing_success_label_success_update
                        ),
                        listingId = ListingId(event.listingId)
                    )
                }
            }

            CreateListingSuccessContract.Event.OnViewListingClicked -> {
                setEffect {
                    NavigateToListingDetailsScreen(
                        listingId = currentState.listingId
                    )
                }
            }

            CreateListingSuccessContract.Event.OnBackClicked -> {
                setEffect {
                    CreateListingSuccessContract.Effect.Navigation.GoBack
                }
            }
        }
    }
}