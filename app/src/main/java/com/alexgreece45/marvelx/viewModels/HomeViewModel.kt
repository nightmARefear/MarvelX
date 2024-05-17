package com.alexgreece45.marvelx.viewModels

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider

sealed class HomeEvent : BaseEvent {
    data class Init(val errorMessage: String?) : HomeEvent()

    data object OnBack : HomeEvent()

    data object OnLogInButtonClick : HomeEvent()

    data object OnSignUpButtonClick : HomeEvent()
}

data class HomeState(
    // Base states.
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Alert Dialog.
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = "",
    val alertDialogErrorMessage: String = "",
    val alertDialogOnClick: () -> Unit
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
            alertDialogOnClick = {
                setState {
                    copy(
                        isAlertDialogVisible = false
                    )
                }
            }
        )

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                event.errorMessage?.let {
                    showAlertDialog(it)
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

    private fun showAlertDialog(errorMessage: String) {
        setState {
            copy(
                isAlertDialogVisible = true,
                alertDialogTitle = resourceProvider.getString(R.string.login_screen_error_view_title),
                alertDialogErrorMessage = errorMessage
            )
        }
    }
}