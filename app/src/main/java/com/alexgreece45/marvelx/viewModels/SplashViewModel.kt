package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.interactors.SplashInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashScreenError(
    val title: String,
    val errorMessage: String,
    val onCloseClick: SplashEvent
)

sealed interface SplashEvent : BaseEvent {
    data object InitView : SplashEvent

    data object OnCloseClick : SplashEvent
}

data class SplashState(
    val isAnimationVisible: Boolean = true,

    val error: SplashScreenError? = null
) : BaseState

sealed interface SplashEffect : BaseEffect {
    sealed interface Navigation : SplashEffect {
        data object NavigateToHomeScreen : Navigation

        data object CloseApplication : Navigation
    }
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val splashInteractor: SplashInteractor
) : BaseViewModel<SplashEvent, SplashState, SplashEffect>() {
    override fun setInitialState(): SplashState = SplashState()

    override fun handleEvents(event: SplashEvent) {
        when (event) {
            SplashEvent.InitView -> {
                viewModelScope.launch {
                    // Purposeful delay so that the GIF animation can be displayed.
                    delay(3000L)

                    // Check if the application is secure.
                    performSecurityChecks()
                }
            }

            SplashEvent.OnCloseClick -> {
                setEffect {
                    SplashEffect.Navigation.CloseApplication
                }
            }
        }
    }

    private suspend fun performSecurityChecks() {
        splashInteractor.isApplicationSecure().collect { isApplicationSecure ->
            when (isApplicationSecure) {
                true -> {
                    setEffect {
                        SplashEffect.Navigation.NavigateToHomeScreen
                    }
                }

                false -> {
                    setState {
                        copy(
                            isAnimationVisible = false,
                            error = SplashScreenError(
                                title = resourceProvider.getString(R.string.splash_screen_alert_dialog_title),
                                errorMessage = resourceProvider.getString(R.string.splash_screen_alert_dialog_error_message),
                                onCloseClick = SplashEvent.OnCloseClick
                            )
                        )
                    }
                }
            }
        }
    }
}