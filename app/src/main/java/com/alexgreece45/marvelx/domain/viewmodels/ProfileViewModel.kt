package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class ProfileEvent : BaseEvent {
    data class InitView(val userId: Int) : ProfileEvent()
}

data class ProfileState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
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
                errorMessage = null
            )
    }
}

sealed class ProfileEffect : BaseEffect {

}

@HiltViewModel
class ProfileViewModel @Inject constructor(): BaseViewModel<ProfileEvent, ProfileState, ProfileEffect>() {
    override fun setInitialState(): ProfileState = ProfileState.getDefaultValues()

    override suspend fun handleEvents(event: ProfileEvent) {
       when (event) {
           is ProfileEvent.InitView -> {
               initView()
           }
       }
    }

    private fun initView() {

    }
}