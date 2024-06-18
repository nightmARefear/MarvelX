package com.alexgreece45.marvelx.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.domain.interactors.GetCurrentUserProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenErrorView
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenSection
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch

sealed interface ProfileContract {
    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnSeeYourProfileClicked : Event
        data object OnProfileImageLoaded : Event
        data object OnErrorClose : Event
    }

    /**
     * @param isLoading Whether the screen is in loading state.
     */
    data class State(
        val isLoading: Boolean = true,

        val profileBannerLabel: String = "",
        val sections: List<ProfileScreenSection> = emptyList(),
        val profileDetails: BasicProfileDetailsDomain? = null,

        val error: ProfileScreenErrorView? = null,
    ) : BaseState

    sealed class Effect : BaseEffect {
        sealed class Navigation : Effect() {
            data object NavigateToHomeScreen : Navigation()
            data class NavigateToProfileDetailsScreen(val userId: Int) : Navigation()
            data object NavigateToPrivacySettingsScreen : Navigation()
            data object NavigateToFaqScreen : Navigation()
        }

        data class ShowToast(val toastMessage: String) : Effect()
    }
}


class ProfileViewModel(
    private val resourceProvider: ResourceProvider,
    private val loginInteractor: LoginInteractor,
    private val userInteractor: UserInteractor,
    private val prefKeys: PrefKeys
) :
    BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {
    override fun setInitialState(): ProfileContract.State = ProfileContract.State()

    override fun handleEvents(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.Init -> {
                init(event)
            }

            ProfileContract.Event.OnErrorClose -> {
                setState {
                    copy(
                        error = null
                    )
                }
            }

            ProfileContract.Event.OnProfileImageLoaded -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
            }

            ProfileContract.Event.OnSeeYourProfileClicked -> {
                setEffect {
                    ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen(prefKeys.getUserId())
                }
            }
        }
    }

    private fun init(event: ProfileContract.Event) {
        setState {
            copy(
                isLoading = true
            )
        }
        initializeSections()

        // Load profile details.
        viewModelScope.launch {
            userInteractor.getCurrentUserProfileDetails().collect {
                when (it) {
                    is GetCurrentUserProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    it.basicProfileDetails.firstName
                                ),
                                profileDetails = it.basicProfileDetails
                            )
                        }
                    }

                    is GetCurrentUserProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    ""
                                )
                            )
                        }
                        showError(
                            errorSubtitle = it.errorMessage,
                            event = event
                        )
                    }

                    GetCurrentUserProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    ""
                                )
                            )
                        }
                        showError(
                            errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                            event = event
                        )
                    }
                }
            }
        }
    }

    /**
     * Initializes the sections.
     */
    private fun initializeSections() {
        setState {
            copy(
                isLoading = false,
                sections = listOf(
                    ProfileScreenSection(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_privacy_settings),
                        iconData = IconData(
                            iconResource = R.drawable.ic_privacy,
                            contentDescription = R.string.content_description_privacy
                        ),
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToPrivacySettingsScreen
                            }
                        }
                    ),
                    ProfileScreenSection(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_faq),
                        iconData = IconData(
                            iconResource = R.drawable.ic_faq,
                            contentDescription = R.string.content_description_faq
                        ),
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToFaqScreen
                            }
                        }
                    ),
                    ProfileScreenSection(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_logout),
                        iconData = IconData(
                            iconResource = R.drawable.ic_logout,
                            contentDescription = R.string.content_description_ic_logout
                        ),
                        isNavigable = false,
                        onClick = ::logout
                    ),
                )
            )
        }
    }

    /**
     * Logouts the user.
     */
    private fun logout() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            loginInteractor.logout().collect { logoutPartialState ->
                when (logoutPartialState) {
                    is LogoutPartialState.Failed -> {
                        // Show error message.
                        setEffect {
                            ProfileContract.Effect.ShowToast(
                                toastMessage = logoutPartialState.errorMessage
                            )
                        }

                        // Navigate to Home Screen.
                        setEffect {
                            ProfileContract.Effect.Navigation.NavigateToHomeScreen
                        }
                    }

                    LogoutPartialState.Success -> {
                        // Navigate to Home Screen.
                        setEffect {
                            ProfileContract.Effect.Navigation.NavigateToHomeScreen
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the error view.
     */
    private fun showError(
        errorTitle: String? = null,
        errorSubtitle: String,
        event: ProfileContract.Event
    ) {
        setState {
            copy(
                error = ProfileScreenErrorView(
                    errorTitle = errorTitle
                        ?: resourceProvider.getString(R.string.profile_screen_error_loading_profile_details),
                    errorSubtitle = errorSubtitle,
                    event = event
                )
            )
        }
    }
}