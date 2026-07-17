package com.alexgreece45.marvelx.ui.screens.myListings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.listing.ListingId
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingUi
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsSelectedListingData
import com.alexgreece45.marvelx.ui.screens.myListings.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MyListingsViewModel(
    private val listingInteractor: ListingInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<MyListingsContract.Event, MyListingsContract.State, MyListingsContract.Effect>() {

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

            MyListingsContract.Event.BottomSheetEvents.OnEditListingClicked -> onBottomSheetListingEditClicked()
            MyListingsContract.Event.BottomSheetEvents.OnDeleteListingClicked -> onBottomSheetListingDeleteClicked()
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

        fetchListings()
    }

    private fun onBackClicked() {
        if (currentState.selectedListingData != null) {
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

        if (!currentState.isScrollingInitialized) {
            setState {
                copy(
                    isScrollingInitialized = true
                )
            }
            return
        }

        setState {
            copy(
                isFetchingNewItems = true,
                currentPage = currentPage + 1
            )
        }
        fetchListings()
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
                selectedListingData = MyListingsSelectedListingData(
                    listingId = listing.listingId,
                    title = listing.title
                )
            )
        }
        setEffect {
            MyListingsContract.Effect.BottomSheetEffects.Show
        }
    }

    private fun onBottomSheetListingEditClicked() {
        val listingData = currentState.selectedListingData

        hideBottomSheet()

        if (listingData == null) return

        setEffect {
            MyListingsContract.Effect.Navigation.GoToEditListingScreen(listingData.listingId)
        }
    }

    private fun onBottomSheetListingDeleteClicked() {
        val listingData = currentState.selectedListingData

        hideBottomSheet()

        if (listingData == null) return

        deleteListing(listingData)
    }

    private fun onErrorButtonClicked() {
        setState {
            copy(
                isLoading = true,
                error = false
            )
        }
        fetchListings()
    }

    /**
     * Helper functions.
     */
    private fun fetchListings() {
        additionalListingsJob = viewModelScope.launch {
            when (
                val result = listingInteractor.getCurrentUserListings(
                    page = currentState.currentPage
                )
            ) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    val isFetchingInitialListings = currentState.currentPage == 1
                    setState {
                        copy(
                            isLoading = false,
                            isFetchingNewItems = false,
                            error = isFetchingInitialListings
                        )
                    }

                    if (!isFetchingInitialListings) {
                        showToastMessage(resourceProvider.getString(R.string.screen_my_listings_error_fetching_additional_listings))
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
                            allListings = allListings.addAll(filteredAdditionalListings.toPersistentList()),
                            error = false
                        )
                    }
                }
            }
        }
    }

    private fun deleteListing(listingData: MyListingsSelectedListingData) {
        val listingId = listingData.listingId

        setState {
            copy(
                allListings = allListings.map { listing ->
                    if (listing.listingId == listingId) {
                        listing.copy(isProcessing = true)
                    } else {
                        listing
                    }
                }.toPersistentList()
            )
        }

        viewModelScope.launch {
            if (listingInteractor.deleteListing(listingId)) {
                setState {
                    copy(
                        allListings = allListings.filterNot { it.listingId == listingId }
                            .toPersistentList()
                    )
                }

                showToastMessage(
                    resourceProvider.getString(
                        R.string.screen_my_listings_success_deleting_listing,
                        listingData.title
                    )
                )
            } else {
                setState {
                    copy(
                        allListings = allListings.map { listing ->
                            if (listing.listingId == listingId) {
                                listing.copy(isProcessing = false)
                            } else {
                                listing
                            }
                        }.toPersistentList()
                    )
                }

                showToastMessage(
                    resourceProvider.getString(
                        R.string.screen_my_listings_error_deleting_listing,
                        listingData.title
                    )
                )
            }
        }
    }

    private fun showToastMessage(message: String) {
        setEffect {
            MyListingsContract.Effect.ShowToastMessage(message)
        }
    }

    private fun hideBottomSheet() {
        setState {
            copy(
                selectedListingData = null
            )
        }
        setEffect {
            MyListingsContract.Effect.BottomSheetEffects.Hide
        }
    }
}
