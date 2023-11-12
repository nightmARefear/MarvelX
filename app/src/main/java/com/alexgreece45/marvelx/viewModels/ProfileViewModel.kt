package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.interactors.LogoutPartialState
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed class ProfileEvent : BaseEvent {
    data object InitView : ProfileEvent()

    data object LoadProfileDetails : ProfileEvent()

    data object OnLogoutButtonPressed : ProfileEvent()
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,

    val bottomNavigationItems: List<BottomBarItem> = emptyList(),

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val prefKeys: PrefKeys,
    private val authController: AuthController
) :
    BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState(
        isLoading = true,
        bottomNavigationItems = getBottomNavigationItems(),
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
                initView()
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
                setState {
                    copy(
                        isLoading = true,
                        error = null
                    )
                }
                loadProfileDetails()
            }
        }
    }

    private fun initView() {
//        setState {
//            copy(
//                error = null
//            )
//        }
        loadProfileDetails()
    }

    private fun loadProfileDetails() {
        setState {
            copy(
                userId = prefKeys.getUserId(),
                accessToken = authController.getAccessToken() ?: ""
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails(userId = currentState.userId).collect {
                when (it) {
                    is GetProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
//                                error = ProfileScreenErrorView(
//                                    errorMessage = it.errorMessage,
//                                    onRetryClick = { loadProfileDetails() }
//                                )
                            )
                        }
                        logout()
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = ProfileScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = {
                                        loadProfileDetails()
                                    }
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                profileDetails = it.profileDetails
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
            userInteractor.logout().collect { logoutPartialState ->
                when (logoutPartialState) {
                    is LogoutPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }

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
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        // Navigate to Home Screen.
                        setEffect {
                            ProfileEffect.Navigation.NavigateToHomeScreen
                        }
                    }
                }
            }
        }
    }

    private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllCharactersScreen.route,
            label = R.string.bottom_navigation_label_characters,
            iconData = IconData(
                iconResource = R.drawable.ic_superhero_32,
                contentDescription = R.string.bottom_navigation_label_characters
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllFavoriteCharactersScreen.route,
            label = R.string.bottom_navigation_label_favorites,
            iconData = IconData(
                iconResource = R.drawable.ic_favorite_filled_24,
                contentDescription = R.string.bottom_navigation_label_favorites
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.ProfileScreen.route,
            label = R.string.bottom_navigation_label_profile,
            iconData = IconData(
                iconResource = R.drawable.ic_person_32,
                contentDescription = R.string.bottom_navigation_label_profile
            )
        )
    )
}