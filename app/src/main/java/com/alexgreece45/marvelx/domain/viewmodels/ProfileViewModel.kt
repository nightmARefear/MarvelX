package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStateGetProfileDetails
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileEvent : BaseEvent {
    data class InitView(val userId: Int) : ProfileEvent()
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,

    /**
     * The user ID.
     * */
    val userId: Int = -1,

    /**
     * The profile details of the user.
     * */
    val profileDetails: UserProfileDetailsDomain? = null,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,
) : BaseState {
    companion object {
        fun getDefaultValues(): ProfileState =
            ProfileState(
                isLoading = true,
                hasInternetConnection = true,
                userId = -1,
                profileDetails = null,
                errorMessage = null
            )
    }
}

sealed class ProfileEffect : BaseEffect {

}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userInteractor: UserInteractor
) :
    BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState.getDefaultValues()

    override fun handleEvents(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.InitView -> {
                initView(userId = event.userId)
            }
        }
    }

    private fun initView(userId: Int) {
        setState {
            copy(
                userId = userId,
                isLoading = true
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails(userId = currentState.userId).collect {
                when (it) {
                    is UserPartialStateGetProfileDetails.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = it.errorMessage
                            )
                        }
                    }
                    is UserPartialStateGetProfileDetails.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
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
}