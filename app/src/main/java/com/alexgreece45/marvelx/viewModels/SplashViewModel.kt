package com.alexgreece45.marvelx.viewModels

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.SplashInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface SplashContract {
    sealed class AlertDialogErrorType(@StringRes val title: Int, @StringRes val errorMessage: Int) {
        data object InsecureApplication : AlertDialogErrorType(
            title = R.string.splash_screen_alert_dialog_title_insecure_application,
            errorMessage = R.string.splash_screen_alert_dialog_error_message_insecure_application
        )

        data object ApplicationInMultiWindowMode : AlertDialogErrorType(
            title = R.string.splash_screen_alert_dialog_title_application_in_multi_window_mode,
            errorMessage = R.string.splash_screen_alert_dialog_error_message_application_in_multi_window_mode
        )
    }

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
                when (val splashAlertDialogErrorTypeNavArg =
                    NavArgHelper.getArgument<SplashContract.AlertDialogErrorType>(NavArg.SplashAlertDialogErrorType)) {
                    is NavArgStatus.Exists -> {
                        showAlertDialog(splashAlertDialogErrorTypeNavArg.value)
                        return
                    }

                    NavArgStatus.NotExists -> {}
                }

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
                                showAlertDialog(SplashContract.AlertDialogErrorType.InsecureApplication)
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

    /**
     * Shows the given [alertDialogErrorType].
     */
    private fun showAlertDialog(alertDialogErrorType: SplashContract.AlertDialogErrorType) {
        setState {
            copy(
                isAnimationVisible = false,
                error = SplashContract.SplashScreenError(
                    title = resourceProvider.getString(alertDialogErrorType.title),
                    errorMessage = resourceProvider.getString(alertDialogErrorType.errorMessage),
                    onCloseClick = SplashContract.Event.OnCloseClick
                )
            )
        }
    }
}