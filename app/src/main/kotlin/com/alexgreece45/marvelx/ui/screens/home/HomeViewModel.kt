package com.alexgreece45.marvelx.ui.screens.home

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.home.models.HomeAlertDialogErrorView
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

sealed class HomeEvent : BaseEvent {
    data class Init(val logoutReason: String?) : HomeEvent()
    data object OnBack : HomeEvent()
    data object OnLoginButtonClicked : HomeEvent()
    data object OnSignupButtonClicked : HomeEvent()
}

data class HomeState(
    val isLoading: Boolean = false,
    val alertDialogError: HomeAlertDialogErrorView? = null,
) : BaseState

sealed interface HomeEffect : BaseEffect {
    sealed class Navigation : HomeEffect {
        data object PopBackStack : Navigation()
        data object NavigateToLoginScreen : Navigation()
        data object NavigateToSignupScreen : Navigation()
    }
}

@KoinViewModel
class HomeViewModel(
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {
    override fun setInitialState(): HomeState = HomeState()

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                // Anything that is not a LogoutReason name (e.g. the "none" sentinel) means the
                // screen was reached normally and there is nothing to announce.
                val logoutReason = LogoutReason.entries.firstOrNull { it.name == event.logoutReason }
                    ?: return

                val (titleResId, descriptionResId) = when (logoutReason) {
                    LogoutReason.SESSION_EXPIRED,
                    LogoutReason.SECURITY_VALIDATION_ERROR -> {
                        R.string.screen_home_alert_dialog_session_expired_title to R.string.screen_home_alert_dialog_session_expired_description
                    }

                    LogoutReason.USER_TRIGGERED -> {
                        R.string.screen_home_alert_dialog_user_logout_title to R.string.screen_home_alert_dialog_user_logout_description
                    }
                }

                showAlertDialog(
                    title = resourceProvider.getString(titleResId),
                    description = resourceProvider.getString(descriptionResId)
                )
            }

            HomeEvent.OnBack -> setEffect { HomeEffect.Navigation.PopBackStack }
            HomeEvent.OnLoginButtonClicked -> setEffect { HomeEffect.Navigation.NavigateToLoginScreen }
            HomeEvent.OnSignupButtonClicked -> setEffect { HomeEffect.Navigation.NavigateToSignupScreen }
        }
    }

    private fun showAlertDialog(title: String, description: String) {
        setState {
            copy(
                alertDialogError = HomeAlertDialogErrorView(
                    title = title,
                    description = description,
                    buttonLabel = resourceProvider.getString(R.string.generic_ok_button),
                    alertDialogOnClick = {
                        setState { copy(alertDialogError = null) }
                    }
                )
            )
        }
    }
}
