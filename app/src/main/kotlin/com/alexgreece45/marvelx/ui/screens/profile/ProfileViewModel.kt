package com.alexgreece45.marvelx.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import kotlinx.coroutines.launch

data class ProfileScreenErrorView(
    val errorMessage: String,
    val profileEvent: ProfileEvent
)

sealed interface ProfileEvent : BaseEvent {
    data object Init : ProfileEvent
    data object OnLogoutButtonPressed : ProfileEvent

    sealed interface Section : ProfileEvent {
        data object OnPersonalInformationClicked : Section
    }
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val error: ProfileScreenErrorView? = null,
) : BaseState

sealed class ProfileEffect : BaseEffect {
    sealed class Navigation : ProfileEffect() {
        data object NavigateToHomeScreen : Navigation()
        data object NavigateToPersonalDetailsScreen : Navigation()
    }

    data class ShowToast(val toastMessage: String) : ProfileEffect()
}

class ProfileViewModel(
    private val loginInteractor: LoginInteractor
) :
    BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState()

    override fun handleEvents(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Init -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initView(event)
            }

            ProfileEvent.OnLogoutButtonPressed -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                logout()
            }

            ProfileEvent.Section.OnPersonalInformationClicked -> {
                setEffect {
                    ProfileEffect.Navigation.NavigateToPersonalDetailsScreen
                }
            }
        }
    }

    private fun initView(event: ProfileEvent) {
//        loadProfileDetails(event)
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