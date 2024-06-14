package com.alexgreece45.marvelx.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
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
        data object OnLogoutButtonPressed : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val sections: List<ProfileScreenSection> = emptyList(),

        val error: ProfileScreenErrorView? = null,
    ) : BaseState

    sealed class Effect : BaseEffect {
        sealed class Navigation : Effect() {
            data object NavigateToHomeScreen : Navigation()
            data object NavigateToProfileDetailsScreen : Navigation()
            data object NavigateToPrivacySettingsScreen : Navigation()
            data object NavigateToFaqScreen : Navigation()
        }

        data class ShowToast(val toastMessage: String) : Effect()
    }
}



class ProfileViewModel(
    private val loginInteractor: LoginInteractor,
    private val resourceProvider: ResourceProvider,
) :
    BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {
    override fun setInitialState(): ProfileContract.State = ProfileContract.State()

    override fun handleEvents(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.Init -> {
                init()
            }

            ProfileContract.Event.OnLogoutButtonPressed -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                logout()
            }
        }
    }

    private fun init() {
        // Initializes the sections.
        setState {
            copy(
                isLoading = false,
                sections = listOf(
                    ProfileScreenSection(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_personal_information),
                        iconData = IconData(
                            iconResource = R.drawable.ic_person,
                            contentDescription = R.string.content_description_person
                        ),
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen
                            }
                        }
                    ),
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
                )
            )
        }
    }

    /**
     * Logouts the user.
     */
    private fun logout() {
        setEffect {
            ProfileContract.Effect.ShowToast("logout button clicked")
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
}