package com.alexgreece45.marvelx.ui.screens.profile

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
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

sealed interface ProfileEvent : BaseEvent {
    data object Init : ProfileEvent
    data object OnLogoutButtonPressed : ProfileEvent
}

data class ProfileState(
    val isLoading: Boolean = true,
    val sections: List<ProfileScreenSection> = emptyList(),

    val error: ProfileScreenErrorView? = null,
) : BaseState

sealed class ProfileEffect : BaseEffect {
    sealed class Navigation : ProfileEffect() {
        data object NavigateToHomeScreen : Navigation()
        data object NavigateToPersonalDetailsScreen : Navigation()
        data object NavigateToFaqScreen : Navigation()
    }

    data class ShowToast(val toastMessage: String) : ProfileEffect()
}

class ProfileViewModel(
    private val loginInteractor: LoginInteractor,
    private val resourceProvider: ResourceProvider,
) :
    BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState()

    override fun handleEvents(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Init -> {
                init()
            }

            ProfileEvent.OnLogoutButtonPressed -> {
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
                                ProfileEffect.Navigation.NavigateToPersonalDetailsScreen
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
                                ProfileEffect.Navigation.NavigateToFaqScreen
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
            ProfileEffect.ShowToast("logout button clicked")
        }

        viewModelScope.launch {
            loginInteractor.logout().collect { logoutPartialState ->
                when (logoutPartialState) {
                    is LogoutPartialState.Failed -> {
                        // Show error message.
                        setEffect {
                            ProfileEffect.ShowToast(
                                toastMessage = logoutPartialState.errorMessage
                            )
                        }

                        // Navigate to Home Screen.
                        setEffect {
                            ProfileEffect.Navigation.NavigateToHomeScreen
                        }
                    }

                    LogoutPartialState.Success -> {
                        // Navigate to Home Screen.
                        setEffect {
                            ProfileEffect.Navigation.NavigateToHomeScreen
                        }
                    }
                }
            }
        }
    }
}