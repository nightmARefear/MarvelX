package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.interactors.SplashInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface SplashContract {
    data class SplashScreenError(
        val title: String,
        val errorMessage: String,
        val onCloseClick: Event
    ) : SplashContract

    sealed interface Event : BaseEvent {
        data object Init : Event

        data object OnCloseClick : Event
    }

    data class State(
        val isAnimationVisible: Boolean = true,

        val error: SplashScreenError? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object NavigateToIntroLoadingScreen : Navigation
            data object NavigateToHomeScreen : Navigation

            data object CloseApplication : Navigation
        }
    }
}


class SplashViewModel(
    private val resourceProvider: ResourceProvider,
    private val splashInteractor: SplashInteractor
) : BaseViewModel<SplashContract.Event, SplashContract.State, SplashContract.Effect>() {
    override fun setInitialState(): SplashContract.State = SplashContract.State()

    override fun handleEvents(event: SplashContract.Event) {
        when (event) {
            SplashContract.Event.Init -> {
                viewModelScope.launch {
                    // Intentional delay for logo to appear for at least 3 seconds.
                    delay(3000L)

                    splashInteractor.isApplicationSecure().collect { isApplicationSecure ->
                        when (isApplicationSecure) {
                            true -> {
                                setEffect {
                                    SplashContract.Effect.Navigation.NavigateToIntroLoadingScreen
                                }
                            }

                            false -> {
                                setState {
                                    copy(
                                        isAnimationVisible = false,
                                        error = SplashContract.SplashScreenError(
                                            title = resourceProvider.getString(R.string.splash_screen_alert_dialog_title),
                                            errorMessage = resourceProvider.getString(R.string.splash_screen_alert_dialog_error_message),
                                            onCloseClick = SplashContract.Event.OnCloseClick
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SplashContract.Event.OnCloseClick -> {
                setEffect {
                    SplashContract.Effect.Navigation.CloseApplication
                }
            }
        }
    }
}