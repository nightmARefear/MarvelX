package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model

import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.util.extensions.model.ShareContentUrlData

sealed interface ListingDetailsContract {

    sealed interface Event : BaseEvent {
        data class Init(
            val listingId: String?,
            val createListingWizardMode: CreateListingWizardMode?
        ) : Event

        data object OnBack : Event

        sealed interface ToolbarActionButton : Event {
            data object OnFavoriteClicked : ToolbarActionButton
            data object OnShareClicked : ToolbarActionButton
            data object OnEditClicked : ToolbarActionButton
        }

        data class OnCarouselScrolled(val index: Int) : Event

        data object OnSellerInfoClicked : Event

        data object OnStickyBottomButtonClicked : Event

        data object OnErrorRetryButtonClicked : Event
    }

    data class State(
        // Loading states.
        val isLoading: Boolean = true,
        val isFavoriteButtonLoading: Boolean = false,

        val mode: ListingDetailsMode? = null,

        val listingId: String? = null,
        val listing: ListingUi? = null,
        val sellerProfileDetails: ProfileDetailsDomain? = null,

        val hasError: Boolean = false,

        val stickyBottomButtonLoading: Boolean = false,
    ) : BaseState {

        val showToolbar: Boolean get() = mode != ListingDetailsMode.OVERVIEW
        val toolbarTitle: String get() = if (isLoading) "" else listing?.title.orEmpty()
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class NavigateToProfileDetailsScreen(val userId: UserId) : Navigation
            data class NavigateToEditListingScreen(val listingId: String) : Navigation
            data class NavigateToOrderItemTrackingHistoryScreen(
                val orderId: String,
                val orderItemId: String
            ) : Navigation

            data class NavigateToOrderItemManagementScreen(
                val orderId: String,
                val orderItemId: String
            ) : Navigation

            data object GoToNextScreen : Navigation
        }

        data class ShowToastMessage(val message: String) : Effect
        data class ShareListingUrl(val shareContentUrlData: ShareContentUrlData) : Effect
    }
}