package com.alexgreece45.marvelx.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import kotlinx.coroutines.launch

data class ProfileScreenErrorView(
    val errorMessage: String,
    val profileEvent: ProfileEvent
)

sealed class ProfileEvent : BaseEvent {
    data object InitView : ProfileEvent()

    data object LoadProfileDetails : ProfileEvent()

    data object OnLogoutButtonPressed : ProfileEvent()
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,

    /**
     * The user ID.
     * */
    val userId: Int = -1,
    val accessToken: String = "",

    /**
     * The profile details of the user.
     * */
    val profileDetails: ProfileDetailsDomain? = null,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val error: ProfileScreenErrorView? = null,
) : BaseState

sealed class ProfileEffect : BaseEffect {
    sealed class Navigation : ProfileEffect() {
        data object NavigateToHomeScreen : Navigation()
    }

    data class ShowToast(val toastMessage: String) : ProfileEffect()
}

class ProfileViewModel(
    private val loginInteractor: LoginInteractor,
    private val userInteractor: UserInteractor,
    private val prefKeys: PrefKeys,
    private val authController: AuthController
) :
    BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState(
        isLoading = true,
        userId = -1,
        profileDetails = null,
        error = null
    )

    override fun handleEvents(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.InitView -> {
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

            ProfileEvent.LoadProfileDetails -> {
                loadProfileDetails(event)
            }
        }
    }

    private fun initView(event: ProfileEvent) {
        setState {
            copy(
                userId = prefKeys.getUserId(),
                accessToken = authController.getAccessToken()
            )
        }
        loadProfileDetails(event)
    }

    private fun loadProfileDetails(event: ProfileEvent) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails().collect {
                when (it) {
                    is GetProfileDetailsPartialState.Failed -> {
                        logout()
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = ProfileScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    profileEvent = event
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = null,
                                profileDetails = it.profileDetailsDomain
                            )
                        }
                    }
                }
            }
        }
    }

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