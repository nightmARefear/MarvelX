package com.alexgreece45.marvelx.ui.screens.splash

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.security.controllers.SecurityController
import com.alexgreece45.marvelx.core.security.model.security.ApplicationSecurity
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
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
import org.koin.android.annotation.KoinViewModel

sealed interface SplashContract {
    sealed class AlertDialogErrorType(
        @StringRes val title: Int,
        @StringRes val errorMessage: Int,
        open val securityViolation: SecurityViolation
    ) {
        data class InsecureApplication(override val securityViolation: SecurityViolation) :
            AlertDialogErrorType(
                title = R.string.splash_screen_alert_dialog_title_insecure_application,
                errorMessage = R.string.splash_screen_alert_dialog_error_message_insecure_application,
                securityViolation = securityViolation
            )

        data object ApplicationInMultiWindowMode : AlertDialogErrorType(
            title = R.string.splash_screen_alert_dialog_title_application_in_multi_window_mode,
            errorMessage = R.string.splash_screen_alert_dialog_error_message_application_in_multi_window_mode,
            securityViolation = SecurityViolation.ApplicationInMultiWindowMode
        )
    }

    data class SplashScreenError(
        val title: String,
        val errorMessage: String,
        val onCloseClick: () -> Unit
    ) : SplashContract

    sealed interface Event : BaseEvent {
        data object Init : Event
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

@KoinViewModel
class SplashViewModel(
    private val resourceProvider: ResourceProvider,
    private val securityController: SecurityController
) : BaseViewModel<SplashContract.Event, SplashContract.State, SplashContract.Effect>() {

    override fun setInitialState(): SplashContract.State = SplashContract.State()

    override fun handleEvents(event: SplashContract.Event) {
        when (event) {
            SplashContract.Event.Init -> {
                when (val splashAlertDialogErrorTypeNavArg =
                    NavArgHelper.getArgument<SplashContract.AlertDialogErrorType>(NavArg.SplashAlertDialogErrorType)) {
                    is NavArgStatus.Exists -> {
                        when (splashAlertDialogErrorTypeNavArg.value.securityViolation) {
                            is SecurityViolation.ApplicationInMultiWindowMode -> showAlertDialog(
                                SplashContract.AlertDialogErrorType.ApplicationInMultiWindowMode
                            )

                            else -> showAlertDialog(splashAlertDialogErrorTypeNavArg.value)
                        }
                        showAlertDialog(splashAlertDialogErrorTypeNavArg.value)
                        return
                    }

                    NavArgStatus.NotExists -> {}
                }

                viewModelScope.launch {
                    // Intentional delay for logo to appear for at least 3 seconds.
                    delay(3000L) // TODO: Remove delay by finding a workaround.

                    when (val applicationSecurity = securityController.isApplicationSecure()) {
                        ApplicationSecurity.Secure -> {
                            setEffect {
                                SplashContract.Effect.Navigation.NavigateToIntroLoadingScreen
                            }
                        }

                        is ApplicationSecurity.Insecure -> {
                            showAlertDialog(
                                SplashContract.AlertDialogErrorType.InsecureApplication(
                                    applicationSecurity.securityViolation
                                )
                            )
                        }
                    }
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
                    errorMessage = resourceProvider.getString(alertDialogErrorType.errorMessage)
                        .plus("\n\nError Code: ${alertDialogErrorType.securityViolation.errorCode}"),
                    onCloseClick = {
                        setEffect {
                            SplashContract.Effect.Navigation.CloseApplication
                        }
                    }
                )
            )
        }
    }
}