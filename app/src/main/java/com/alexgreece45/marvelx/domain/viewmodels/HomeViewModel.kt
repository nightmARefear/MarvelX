package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class HomeEvent : BaseEvent {
    object InitView : HomeEvent()

    object OnLogInButtonClick : HomeEvent()
    object OnSignUpButtonClick : HomeEvent()
}

data class HomeState(
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null
) : BaseState

sealed class HomeEffect : BaseEffect {
    object NavigateToLogInScreen : HomeEffect()
    object NavigateToSignUpScreen : HomeEffect()
}

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {
    override fun setInitialState(): HomeState =
        HomeState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null
        )

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            HomeEvent.InitView -> {
                initView()
            }

            HomeEvent.OnLogInButtonClick -> {
                onLogInButtonClick()
            }

            HomeEvent.OnSignUpButtonClick -> {
                onSignUpButtonClick()
            }
        }
    }

    private fun initView() {}

    private fun onLogInButtonClick() {
        setEffect {
            HomeEffect.NavigateToLogInScreen
        }
    }

    private fun onSignUpButtonClick() {
        setEffect {
            HomeEffect.NavigateToSignUpScreen
        }
    }
}