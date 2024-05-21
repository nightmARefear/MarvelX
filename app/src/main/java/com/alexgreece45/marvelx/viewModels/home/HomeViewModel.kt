package com.alexgreece45.marvelx.viewModels.home

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.NavItem
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.viewModels.home.models.HomeAlertDialogErrorType
import com.alexgreece45.marvelx.viewModels.home.models.HomeAlertDialogErrorView

sealed class HomeEvent : BaseEvent {
    data object Init : HomeEvent()

    data object OnBack : HomeEvent()

    data object OnLogInButtonClick : HomeEvent()

    data object OnSignUpButtonClick : HomeEvent()
}

data class HomeState(
    // Base states.
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
    override fun setInitialState(): HomeState =
        HomeState(

        )

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                when (val hasSessionExpiredNavArg =
                    NavArgHelper.getArgument<Boolean>(NavItem.HasSessionExpired)) {
                    is NavArgStatus.Exists -> {
                        if (hasSessionExpiredNavArg.value) {
                            when (val showErrorDialogNavArg =
                                NavArgHelper.getArgument<Boolean>(NavItem.ShowErrorDialog)) {
                                is NavArgStatus.Exists -> {
                                    if (showErrorDialogNavArg.value) {
                                        showAlertDialog(
                                            HomeAlertDialogErrorType.SessionExpired
                                        )
                                    }
                                }

                                NavArgStatus.NotExists -> {}
                            }
                        }
                    }

                    NavArgStatus.NotExists -> {}
                }

                when (val errorMessageNavArg =
                    NavArgHelper.getArgument<String?>(NavItem.ErrorMessage)) {
                    is NavArgStatus.Exists -> {
                        errorMessageNavArg.value
                    }

                    NavArgStatus.NotExists -> {
                        null
                    }
                }?.let { errorMessage ->
                    showAlertDialog(HomeAlertDialogErrorType.HasError(errorMessage = errorMessage))
                }
            }

            HomeEvent.OnBack -> {
                setEffect {
                    HomeEffect.Navigation.PopBackStack
                }
            }

            HomeEvent.OnLogInButtonClick -> {
                setEffect {
                    HomeEffect.Navigation.NavigateToLoginScreen
                }
            }

            HomeEvent.OnSignUpButtonClick -> {
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
                    is HomeAlertDialogErrorType.HasError -> HomeAlertDialogErrorView(
                        title = resourceProvider.getString(R.string.login_screen_error_view_title),
                        errorMessage = homeAlertDialogErrorType.errorMessage,
                        buttonLabel = resourceProvider.getString(R.string.generic_ok_button),
                        alertDialogOnClick = {
                            setState {
                                copy(
                                    alertDialogError = null
                                )
                            }
                        }
                    )

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