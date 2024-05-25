package com.alexgreece45.marvelx.ui.screens.home

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.ui.screens.home.models.HomeAlertDialogErrorType
import com.alexgreece45.marvelx.ui.screens.home.models.HomeAlertDialogErrorView

sealed class HomeEvent : BaseEvent {
    data object Init : HomeEvent()
    data object OnBack : HomeEvent()
    data object OnLoginButtonClicked : HomeEvent()
    data object OnSignupButtonClicked : HomeEvent()
}

data class HomeState(
    val hasInternetConnection: Boolean = true,
    val alertDialogError: HomeAlertDialogErrorView? = null,
) : BaseState

sealed interface HomeEffect : BaseEffect {
    sealed class Navigation : HomeEffect {
        data object PopBackStack : Navigation()
        data object NavigateToLoginScreen : Navigation()
        data object NavigateToSignupScreen : Navigation()
        data class NavigateToOtpScreen(
            val userId: Int,
            val otpFlowType: String,
            val verificationStep: String,
            val shouldRememberUserSession: Boolean
        ) : Navigation()

        data object NavigateToAllCharactersScreen : Navigation()
    }
}

class HomeViewModel(
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {
    override fun setInitialState(): HomeState = HomeState()

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                when (val homeAlertDialogErrorTypeNavArg =
                    NavArgHelper.getArgument<HomeAlertDialogErrorType>(NavArg.HomeAlertDialogErrorType)) {
                    is NavArgStatus.Exists -> {
                        showAlertDialog(homeAlertDialogErrorTypeNavArg.value)
                    }

                    NavArgStatus.NotExists -> {}
                }
            }

            HomeEvent.OnBack -> {
                setEffect {
                    HomeEffect.Navigation.PopBackStack
                }
            }

            HomeEvent.OnLoginButtonClicked -> {
                setEffect {
                    HomeEffect.Navigation.NavigateToLoginScreen
                }
            }

            HomeEvent.OnSignupButtonClicked -> {
                setEffect {
                    HomeEffect.Navigation.NavigateToSignupScreen
                }
            }
        }
    }

    private fun showAlertDialog(homeAlertDialogErrorType: HomeAlertDialogErrorType) {
        setState {
            copy(
                alertDialogError = when (homeAlertDialogErrorType) {
                    is HomeAlertDialogErrorType.HasError -> {
                        homeAlertDialogErrorType.errorMessage?.let { errorMessage ->
                            HomeAlertDialogErrorView(
                                title = resourceProvider.getString(R.string.login_screen_error_view_title),
                                errorMessage = errorMessage,
                                buttonLabel = resourceProvider.getString(R.string.generic_ok_button),
                                alertDialogOnClick = {
                                    setState {
                                        copy(
                                            alertDialogError = null
                                        )
                                    }
                                }
                            )
                        }
                    }

                    HomeAlertDialogErrorType.SessionExpired -> HomeAlertDialogErrorView(
                        title = resourceProvider.getString(R.string.session_status_session_has_expired_alert_dialog_title),
                        errorMessage = resourceProvider.getString(R.string.session_status_session_has_expired_alert_dialog_error_message),
                        buttonLabel = resourceProvider.getString(R.string.generic_close_button),
                        alertDialogOnClick = {
                            setState {
                                copy(
                                    alertDialogError = null
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}