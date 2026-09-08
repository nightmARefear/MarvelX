package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.configuration.ConfigPriceLogic
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.controllers.createListingWizard.CreateListingWizardController
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.onAnyError
import com.alexgreece45.marvelx.domain.helper.onSuccess
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.cart.CartInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.domain.interactors.order.OrderInteractor
import com.alexgreece45.marvelx.domain.model_api.order.toData
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsMode
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.ui.screens.orders.model.item.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.model.ShareContentUrlData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [ListingDetailsViewModel::class])
internal class ListingDetailsViewModel(
    private val listingInteractor: ListingInteractor,
    private val listingUiMapper: ListingUiMapper,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
    private val userSessionRepository: UserSessionRepository,
    private val createListingWizardController: CreateListingWizardController,
    private val cartInteractor: CartInteractor,
    private val appLogger: AppLogger,
    snackbarDelegate: SnackbarDelegate,
    private val orderInteractor: OrderInteractor,
    private val configPriceLogic: ConfigPriceLogic,
) : BaseViewModel<ListingDetailsContract.Event, ListingDetailsContract.State, ListingDetailsContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    override fun setInitialState(): ListingDetailsContract.State = ListingDetailsContract.State()

    override fun handleEvents(event: ListingDetailsContract.Event) {
        when (event) {
            is ListingDetailsContract.Event.Init -> init(
                event.listingId,
                event.createListingWizardMode
            )

            ListingDetailsContract.Event.OnBack -> onBack()
            ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked -> onFavoriteButtonClicked()
            ListingDetailsContract.Event.ToolbarActionButton.OnShareClicked -> onShareButtonClicked()
            ListingDetailsContract.Event.ToolbarActionButton.OnEditClicked -> onEditButtonClicked()
            is ListingDetailsContract.Event.OnCarouselScrolled -> onCarouselScrolled(event.index)
            ListingDetailsContract.Event.OnSellerInfoClicked -> onSellerInfoClicked()
            ListingDetailsContract.Event.OnOrderItemStatusContainerClickableTextClicked -> onOrderItemStatusContainerClickableTextClicked()
            ListingDetailsContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()
            ListingDetailsContract.Event.OnErrorRetryButtonClicked -> onErrorRetryButtonClicked()
        }
    }

    /**
     * Event functions.
     */

    private fun init(listingId: String?, createListingWizardMode: CreateListingWizardMode?) {
        setState {
            copy(
                isLoading = true
            )
        }

        if (listingId != null && createListingWizardMode != null) {
            appLogger.e("ListingDetailsViewModel") { "You must either set \"listingId\" or \"createListingWizardMode\" to not null." }

            setEffect {
                ListingDetailsContract.Effect.ShowToastMessage(resourceProvider.getGenericErrorMessage())
            }
            setEffect {
                ListingDetailsContract.Effect.Navigation.GoBack
            }
            return
        }

        when {
            listingId != null -> {
                if (listingId.isBlank()) {
                    exitWithError()
                } else {
                    setState {
                        copy(
                            mode = ListingDetailsMode.EXISTING,
                            listingId = listingId
                        )
                    }
                    getListing()
                }
            }

            createListingWizardMode != null -> {
                setState {
                    copy(
                        mode = ListingDetailsMode.OVERVIEW
                    )
                }
                loadDraftListing(createListingWizardMode)
            }
        }
    }

    private fun onBack() {
        setEffect {
            ListingDetailsContract.Effect.Navigation.GoBack
        }
    }

    private fun onFavoriteButtonClicked() {
        if (currentState.isFavoriteButtonLoading) return

        when (val listing = currentState.listing) {
            null -> {
                return
            }

            else -> {
                setState {
                    copy(
                        isFavoriteButtonLoading = true
                    )
                }
                viewModelScope.launch {
                    if (listing.favorite) {
                        val wasRemoved = listingInteractor.removeFromFavorites(listing.listingId)
                        if (wasRemoved) {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false,
                                    listing = listing.copy(favorite = false)
                                )
                            }
                        } else {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false
                                )
                            }
                            showErroneousSnackbar(
                                errorMessage = resourceProvider.getString(R.string.screen_listing_details_error_removing_from_favorites)
                            )
                        }
                    } else {
                        val wasAdded = listingInteractor.addToFavorites(listing.listingId)
                        if (wasAdded) {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false,
                                    listing = listing.copy(favorite = true)
                                )
                            }
                        } else {
                            setState {
                                copy(
                                    isFavoriteButtonLoading = false
                                )
                            }
                            showErroneousSnackbar(
                                errorMessage = resourceProvider.getString(R.string.screen_listing_details_error_adding_to_favorites)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onShareButtonClicked() {
        currentState.listing?.let { listing ->
            setEffect {
                ListingDetailsContract.Effect.ShareListingUrl(
                    ShareContentUrlData(
                        deepLinkWebUrl = DeepLinkWebUrl.ListingDetails,
                        urlSuffix = listing.listingId,
                        title = resourceProvider.getString(
                            R.string.screen_listing_details_share_intent_title,
                            listing.title
                        )
                    )
                )
            }
        }
    }

    private fun onEditButtonClicked() {
        currentState.listingId?.let { listingId ->
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToEditListingScreen(listingId)
            }
        }
    }

    private fun onCarouselScrolled(index: Int) {
        setState {
            copy(
                listing = listing?.copy(
                    selectedImageIndex = index
                )
            )
        }
    }

    private fun onSellerInfoClicked() {
        currentState.listing?.sellerUserId?.let { userId ->
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToProfileDetailsScreen(userId)
            }
        }
    }

    private fun onOrderItemStatusContainerClickableTextClicked() {
        if (currentState.listingOrderItem == null) return
        val canEdit = currentState.listing?.canEdit ?: return
        val orderIds = currentState.listing?.orderIds ?: return

        if (canEdit) {
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToOrderItemManagementScreen(
                    orderId = orderIds.orderId.value,
                    orderItemId = orderIds.orderItemId.value
                )
            }
        } else {
            setEffect {
                ListingDetailsContract.Effect.Navigation.NavigateToOrderItemTrackingHistoryScreen(
                    orderId = orderIds.orderId.value,
                    orderItemId = orderIds.orderItemId.value
                )
            }
        }
    }

    private fun onStickyBottomButtonClicked() {
        val (isInCart, listingId) = (currentState.listing?.cart to currentState.listing?.listingId)
        if (isInCart == null || listingId == null) return

        val orderIds = currentState.listing?.orderIds
        if (orderIds != null) {
            if (currentState.listing?.canEdit == true) {
                setEffect {
                    ListingDetailsContract.Effect.Navigation.NavigateToOrderItemManagementScreen(
                        orderId = orderIds.orderId.value,
                        orderItemId = orderIds.orderItemId.value
                    )
                }
            } else {
                setEffect {
                    ListingDetailsContract.Effect.Navigation.NavigateToOrderItemTrackingHistoryScreen(
                        orderId = orderIds.orderId.value,
                        orderItemId = orderIds.orderItemId.value
                    )
                }
            }
            return
        }

        setState {
            copy(
                stickyBottomButtonLoading = true
            )
        }
        viewModelScope.launch {
            if (isInCart) {
                if (cartInteractor.deleteCartItem(listingId)) {
                    setState {
                        copy(
                            stickyBottomButtonLoading = false,
                            listing = listing?.copy(cart = false)
                        )
                    }
                    showSuccessSnackbar(message = resourceProvider.getString(R.string.screen_listing_details_success_removing_from_cart))
                } else {
                    setState {
                        copy(
                            stickyBottomButtonLoading = false
                        )
                    }
                    showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_listing_details_error_removing_from_cart))
                }
            } else {
                if (cartInteractor.addCartItem(listingId)) {
                    setState {
                        copy(
                            stickyBottomButtonLoading = false,
                            listing = listing?.copy(
                                cart = true
                            )
                        )
                    }
                    showSuccessSnackbar(resourceProvider.getString(R.string.screen_listing_details_success_adding_to_cart))
                } else {
                    setState {
                        copy(
                            stickyBottomButtonLoading = false
                        )
                    }
                    showErroneousSnackbar(resourceProvider.getString(R.string.screen_listing_details_error_adding_to_cart))
                }
            }
        }
    }

    private fun onErrorRetryButtonClicked() {
        getListing()
    }

    /**
     * Helper functions.
     */

    /**
     * Loads the listing that the user is currently modifying.
     */
    private fun loadDraftListing(createListingWizardMode: CreateListingWizardMode) {
        viewModelScope.launch {
            val listingUi = when (createListingWizardMode) {
                CreateListingWizardMode.CREATE -> {
                    listingUiMapper.toUi(
                        createListingUi = createListingWizardController.createListingUi,
                        currentUserId = userSessionRepository.getUserId()
                    )
                }

                CreateListingWizardMode.UPDATE -> createListingWizardController.updateListingUi
            }

            if (listingUi == null) {
                exitWithError()
                return@launch
            }

            when (val sellerInfo =
                userInteractor.getProfileDetails(UserId(userSessionRepository.getUserId()))
                    .first()) { // TODO: Add offline way to get cached profile details.
                is GetProfileDetailsPartialState.Error,
                is GetProfileDetailsPartialState.Failed,
                GetProfileDetailsPartialState.NoInternetConnection -> {
                    exitWithError()
                }

                is GetProfileDetailsPartialState.Success -> {
                    setState {
                        copy(
                            isLoading = false,
                            listing = listingUi,
                            sellerProfileDetails = sellerInfo.profileDetailsDomain
                        )
                    }
                }
            }
        }
    }

    private fun getListing() {
        val listingId = currentState.listingId ?: return

        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        viewModelScope.launch {
            when (val partialState = listingInteractor.getListing(listingId)) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    showGenericErrorMessage()
                }

                is InteractorPartialState.Success -> {
                    when (
                        val listing =
                            listingUiMapper.toUi(
                                listing = partialState.data,
                                currentUserId = userSessionRepository.getUserId()
                            )
                    ) {
                        null -> {
                            showGenericErrorMessage()
                        }

                        else -> {
                            // Get seller info.
                            when (val profileDetailsPartialState =
                                userInteractor.getProfileDetails(listing.sellerUserId).first()) {
                                is GetProfileDetailsPartialState.Error,
                                is GetProfileDetailsPartialState.Failed,
                                GetProfileDetailsPartialState.NoInternetConnection -> {
                                    showGenericErrorMessage()
                                }

                                is GetProfileDetailsPartialState.Success -> {
                                    val orderIds = listing.orderIds
                                    if (orderIds == null) {
                                        setState {
                                            copy(
                                                isLoading = false,
                                                listing = listing,
                                                sellerProfileDetails = profileDetailsPartialState.profileDetailsDomain
                                            )
                                        }
                                    } else {
                                        orderInteractor.getOrderItemDetails(
                                            orderId = orderIds.orderId.toData(),
                                            orderItemId = orderIds.orderItemId.toData()
                                        )
                                            .onSuccess { orderItem ->
                                                val orderItemUi =
                                                    orderItem.toUi(priceScale = configPriceLogic.priceScale)
                                                setState {
                                                    copy(
                                                        isLoading = false,
                                                        listing = listing,
                                                        sellerProfileDetails = profileDetailsPartialState.profileDetailsDomain,
                                                        listingOrderItem = orderItemUi
                                                    )
                                                }
                                            }
                                            .onAnyError {
                                                showGenericErrorMessage()
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showGenericErrorMessage() {
        setState {
            copy(
                isLoading = false,
                hasError = true
            )
        }
    }

    private fun exitWithError() {
        // TODO: Convert to global snackbar.
        setEffect {
            ListingDetailsContract.Effect.ShowToastMessage(resourceProvider.getString(R.string.screen_listing_details_error_loading_listing))
        }
        setEffect {
            ListingDetailsContract.Effect.Navigation.GoBack
        }
    }
}