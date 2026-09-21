package com.alexgreece45.marvelx.ui.screens.myListings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.myListings.model.DeleteListingDialogData
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingUi
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui.screens.myListings.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.model.ShareContentUrlData
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [MyListingsViewModel::class])
class MyListingsViewModel(
    private val listingInteractor: ListingInteractor,
    private val resourceProvider: ResourceProvider,
    snackbarDelegate: SnackbarDelegate,
) : BaseViewModel<MyListingsContract.Event, MyListingsContract.State, MyListingsContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    private var additionalListingsJob: Job? = null

    override fun setInitialState(): MyListingsContract.State = MyListingsContract.State()

    override fun handleEvents(event: MyListingsContract.Event) {
        when (event) {
            MyListingsContract.Event.Init -> init()
            MyListingsContract.Event.OnBackClicked -> onBackClicked()
            MyListingsContract.Event.OnFabButtonClicked -> onFabButtonClicked()
            MyListingsContract.Event.OnEndOfListScrolled -> onEndOfListScrolled()
            MyListingsContract.Event.OnCreateListingButtonClicked -> onCreateListingButtonClicked()
            is MyListingsContract.Event.ListingEvents.OnClicked -> onListingClicked(event.listingId)
            is MyListingsContract.Event.ListingEvents.OnMoreActionsButtonClicked -> onListingMoreActionsButtonClicked(
                event.listing
            )

            MyListingsContract.Event.BottomSheet.OnEditListingClicked -> onBottomSheetListingEditClicked()
            MyListingsContract.Event.BottomSheet.OnShareListingClicked -> onBottomSheetListingShareClicked()
            MyListingsContract.Event.BottomSheet.OnDeleteListingClicked -> onBottomSheetListingDeleteClicked()
            MyListingsContract.Event.OnDeleteListingDialogNegativeButtonClicked -> onDeleteListingDialogNegativeButtonClicked()
            MyListingsContract.Event.OnDeleteListingDialogPositiveButtonClicked -> onDeleteListingDialogPositiveButtonClicked()
            MyListingsContract.Event.OnErrorButtonClicked -> onErrorButtonClicked()
        }
    }

    private fun init() {
        additionalListingsJob?.cancel()
        setState {
            copy(
                isLoading = true,
                currentPage = 1
            )
        }

        fetchListings(page = 1)
    }

    private fun onBackClicked() {
        if (currentState.selectedListing != null) {
            hideBottomSheet()
        } else {
            setEffect {
                MyListingsContract.Effect.Navigation.GoBack
            }
        }
    }

    private fun onFabButtonClicked() {
        setEffect {
            MyListingsContract.Effect.Navigation.GoToCreateListingScreen
        }
    }

    private fun onEndOfListScrolled() {
        if (currentState.isLoading
            || currentState.isFetchingNewItems
            || currentState.allListings.isEmpty()
        ) return

        setState {
            copy(isFetchingNewItems = true)
        }
        fetchListings(page = currentState.currentPage + 1)
    }

    private fun onCreateListingButtonClicked() {
        setEffect {
            MyListingsContract.Effect.Navigation.GoToCreateListingScreen
        }
    }

    private fun onListingClicked(listingId: String) {
        setEffect {
            MyListingsContract.Effect.Navigation.GoToListingDetailsScreen(listingId)
        }
    }

    private fun onListingMoreActionsButtonClicked(listing: MyListingUi) {
        setState {
            copy(
                selectedListing = listing
            )
        }
        setEffect {
            MyListingsContract.Effect.ShowBottomSheet
        }
    }

    private fun onBottomSheetListingEditClicked() {
        val listing = currentState.selectedListing

        hideBottomSheet()

        if (listing == null) return

        setEffect {
            MyListingsContract.Effect.Navigation.GoToEditListingScreen(listing.listingId)
        }
    }

    private fun onBottomSheetListingShareClicked() {
        val listing = currentState.selectedListing

        hideBottomSheet()

        if (listing != null) {
            setEffect {
                MyListingsContract.Effect.ShareListing(
                    ShareContentUrlData(
                        deepLinkWebUrl = DeepLinkWebUrl.ListingDetails,
                        urlSuffix = listing.listingId,
                        title = resourceProvider.getString(R.string.screen_my_listings_share_listing_title, listing.title),
                    )
                )
            }
        }
    }

    private fun onBottomSheetListingDeleteClicked() {
        if (currentState.deleteListingDialogData != null) {
            // Another listing deletion informative dialog is currently visible.
            // Silently drop this event, so the current dialog does not shift to the new one.
            hideBottomSheet()
            return
        }

        setEffect {
            MyListingsContract.Effect.HideBottomSheet
        }

        setState {
            copy(
                selectedListing = null,
                deleteListingDialogData = selectedListing?.let { selectedListing ->
                    DeleteListingDialogData(
                        listingId = selectedListing.listingId,
                        title = selectedListing.title
                    )
                }
            )
        }
    }

    private fun onDeleteListingDialogNegativeButtonClicked() {
        setState {
            copy(
                deleteListingDialogData = null
            )
        }
    }

    private fun onDeleteListingDialogPositiveButtonClicked() {
        val deleteListingDialogData = currentState.deleteListingDialogData

        if (deleteListingDialogData != null) {
            setState {
                copy(
                    deleteListingDialogData = null
                )
            }
            deleteListing(deleteListingDialogData = deleteListingDialogData)
        }
    }

    private fun onErrorButtonClicked() {
        setState {
            copy(
                isLoading = true,
                error = false
            )
        }
        fetchListings(page = currentState.currentPage)
    }

    /**
     * Helper functions.
     */
    /**
     * Fetches [page], leaving [MyListingsContract.State.currentPage] untouched until the page
     * actually arrives. Advancing it up front would permanently skip a page whose request failed:
     * the next end-of-list scroll would ask for the page after it, and those listings would never
     * be shown.
     */
    private fun fetchListings(page: Int) {
        additionalListingsJob = viewModelScope.launch {
            when (
                val result = listingInteractor.getCurrentUserListings(
                    page = page
                )
            ) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    val isFetchingInitialListings = page == 1
                    setState {
                        copy(
                            isLoading = false,
                            isFetchingNewItems = false,
                            error = isFetchingInitialListings
                        )
                    }

                    if (!isFetchingInitialListings) {
                        showErroneousSnackbar(
                            errorMessage = resourceProvider.getString(R.string.screen_my_listings_error_fetching_additional_listings),
                            snackbarActionData = SnackbarActionData(
                                label = resourceProvider.getString(R.string.generic_ok_button),
                                actionType = SnackbarActionType.Hide
                            )
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val additionalListings = result.data.map { it.toUi() }

                    val existingListingIds = currentState.allListings.map { it.listingId }
                    val filteredAdditionalListings =
                        if (existingListingIds.isEmpty()) additionalListings else
                            additionalListings.filterNot { it.listingId in existingListingIds }

                    setState {
                        copy(
                            isLoading = false,
                            isFetchingNewItems = false,
                            currentPage = page,
                            allListings = allListings.addAll(filteredAdditionalListings.toPersistentList()),
                            error = false
                        )
                    }
                }
            }
        }
    }

    private fun deleteListing(deleteListingDialogData: DeleteListingDialogData) {
        setState {
            copy(
                allListings = allListings.map { listing ->
                    if (listing.listingId == deleteListingDialogData.listingId) {
                        listing.copy(isProcessing = true)
                    } else {
                        listing
                    }
                }.toPersistentList()
            )
        }

        viewModelScope.launch {
            if (listingInteractor.deleteListing(deleteListingDialogData.listingId)) {
                setState {
                    copy(
                        allListings = allListings.filterNot { it.listingId == deleteListingDialogData.listingId }
                            .toPersistentList()
                    )
                }

                showSuccessSnackbar(
                    message = resourceProvider.getString(
                        R.string.screen_my_listings_success_deleting_listing,
                        deleteListingDialogData.title
                    ),
                    snackbarActionData = SnackbarActionData(
                        label = resourceProvider.getString(R.string.generic_ok_button),
                        actionType = SnackbarActionType.Hide
                    )
                )
            } else {
                setState {
                    copy(
                        allListings = allListings.map { listing ->
                            if (listing.listingId == deleteListingDialogData.listingId) {
                                listing.copy(isProcessing = false)
                            } else {
                                listing
                            }
                        }.toPersistentList()
                    )
                }

                showErroneousSnackbar(
                    errorMessage = resourceProvider.getString(
                        R.string.screen_my_listings_error_deleting_listing,
                        deleteListingDialogData.title
                    ),
                    snackbarActionData = SnackbarActionData(
                        label = resourceProvider.getString(R.string.generic_ok_button),
                        actionType = SnackbarActionType.Hide
                    )
                )
            }
        }
    }

    private fun hideBottomSheet() {
        setState {
            copy(
                selectedListing = null
            )
        }
        setEffect {
            MyListingsContract.Effect.HideBottomSheet
        }
    }
}