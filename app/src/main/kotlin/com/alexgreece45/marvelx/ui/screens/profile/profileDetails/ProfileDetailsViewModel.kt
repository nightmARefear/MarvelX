package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch

sealed interface ProfileDetailsContract {
    /**
     * Alert dialog error data.
     */
    data class AlertDialogErrorData(
        val errorSubtitle: String,
        val onPositiveClick: () -> Unit
    )

    /**
     * Error view data.
     */
    data class ErrorView(
        val errorTitle: String,
        val errorSubtitle: String,
        val onCloseClicked: Event,
        val errorButtonLayout: ErrorButtonLayout.Single
    )

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnEditProfileDetailsIconClicked : Event

        sealed interface ProfileBanner : Event {
            data object OnFriendsListClicked : ProfileBanner
            data object OnListingsListClicked : ProfileBanner
            data object OnFavoritesListClicked : ProfileBanner
        }
    }

    /**
     * @param isLoading                     Whether this screen is in loading state. It has the
     * default value of `true`.
     * @param profileDetails                The profile details of the user. It has the default
     * value of `null` indicating no user data.
     */
    data class State(
        val isLoading: Boolean = true,

        val isGenderDropDownExpanded: Boolean = false,
        val profileDetails: ProfileDetailsDomain? = null,

        val alertDialogError: AlertDialogErrorData? = null,
        val errorView: ErrorView? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
            data object NavigateToEditProfileDetailsScreen : Navigation
            data object NavigateToFriendsScreen : Navigation
            data object NavigateToListingsScreen : Navigation
            data object NavigateToFavoritesScreen : Navigation
        }
    }
}

class ProfileDetailsViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<ProfileDetailsContract.Event, ProfileDetailsContract.State, ProfileDetailsContract.Effect>() {
    override fun setInitialState(): ProfileDetailsContract.State =
        ProfileDetailsContract.State()

    override fun handleEvents(event: ProfileDetailsContract.Event) {
        when (event) {
            ProfileDetailsContract.Event.Init -> {
                loadProfileDetails(event)
            }

            ProfileDetailsContract.Event.OnBackClicked -> {
                setEffect {
                    ProfileDetailsContract.Effect.Navigation.PopBackStack
                }
            }

            ProfileDetailsContract.Event.OnEditProfileDetailsIconClicked -> {
                setEffect {
                    ProfileDetailsContract.Effect.Navigation.NavigateToEditProfileDetailsScreen
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isFriendsListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToFriendsScreen
                    }
                } else {
                    showAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_friends))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isListingsListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToListingsScreen
                    }
                } else {
                    showAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_listings))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isFavoritesListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToFavoritesScreen
                    }
                } else {
                    showAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_favorites))
                }
            }
        }
    }

    /**
     * Loads the profile details.
     */
    private fun loadProfileDetails(event: ProfileDetailsContract.Event) {
        setState {
            copy(
                isLoading = true,
                errorView = null
            )
        }
        viewModelScope.launch {
            userInteractor.getCurrentUserProfileDetails().collect {
                when (it) {
                    is GetProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = null,
                                profileDetails = it.profileDetailsDomain
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Displays an alert dialog error with the provided [errorSubtitle].
     */
    private fun showAlertDialogError(errorSubtitle: String) {

    }

    /**
     * Hides the alert dialog error.
     */
    private fun hideAlertDialogError() {
        setState {
            copy(
                alertDialogError = null
            )
        }
    }
}
