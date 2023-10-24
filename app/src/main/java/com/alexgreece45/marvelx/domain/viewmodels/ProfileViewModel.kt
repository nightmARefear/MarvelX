package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStateGetProfileDetails
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.BottomBarItem
import com.alexgreece45.marvelx.ui.IconData
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed class ProfileEvent : BaseEvent {
    data class InitView(val userId: Int) : ProfileEvent()

    object LoadProfileDetails : ProfileEvent()

    object OnLogoutButtonPressed : ProfileEvent()
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,

    val bottomNavigationItems: List<BottomBarItem> = emptyList(),

    /**
     * The user ID.
     * */
    val userId: Int = -1,
    val userToken: String = "",

    /**
     * The profile details of the user.
     * */
    val profileDetails: UserProfileDetailsDomain? = null,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val error: ProfileScreenErrorView? = null,
) : BaseState

sealed class ProfileEffect : BaseEffect {
    sealed class Navigation : ProfileEffect() {
        object NavigateToHomeScreen : Navigation()
    }

    data class ShowToast(val toastMessage: String) : ProfileEffect()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val preferencesKeys: PreferencesKeys
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
                initView()
            }

            ProfileEvent.OnLogoutButtonPressed -> {
                logout()
            }

            ProfileEvent.LoadProfileDetails -> {
                setState {
                    copy(
                        error = null
                    )
                }
                loadProfileDetails()
            }
        }
    }

    private fun initView() {
        setState {
            copy(
                error = null
            )
        }
        loadProfileDetails()
    }

    private fun loadProfileDetails() {
        setState {
            copy(
                isLoading = true,
                userId = preferencesKeys.getUserId(),
                userToken = preferencesKeys.getUserToken()
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails(userId = currentState.userId).collect {
                when (it) {
                    is UserPartialStateGetProfileDetails.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = ProfileScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { loadProfileDetails() })
                            )
                        }
                    }

                    is UserPartialStateGetProfileDetails.NoInternetConnection -> {
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

                    is UserPartialStateGetProfileDetails.Success -> {
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

        // Remove user credentials.
        preferencesKeys.removeUserCredentials()

        // Navigate to Home Screen.
        setEffect {
            ProfileEffect.Navigation.NavigateToHomeScreen
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