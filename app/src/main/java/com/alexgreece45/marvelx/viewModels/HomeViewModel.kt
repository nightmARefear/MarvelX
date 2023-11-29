package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.interactors.LogoutPartialState
import com.alexgreece45.marvelx.interactors.PostLoginWithTokenPartialState
import com.alexgreece45.marvelx.interactors.RefreshAccessTokenPartialState
import com.alexgreece45.marvelx.interactors.RegisterInteractor
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeEvent : BaseEvent {
    data object InitView : HomeEvent()

    data object OnBack : HomeEvent()

    sealed class AlertDialog : HomeEvent() {
        data object OnOkClick : AlertDialog()
    }

    data object OnLogInButtonClick : HomeEvent()

    data object OnSignUpButtonClick : HomeEvent()

    data object OnProceedWithLogin : HomeEvent()
}

data class HomeState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Alert Dialog.
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = "",
    val alertDialogErrorMessage: String = "",
) : BaseState

sealed interface HomeEffect : BaseEffect {
    sealed class Navigation : HomeEffect {
        data object PopBackStack : Navigation()
        data object NavigateToLogInScreen : Navigation()
        data object NavigateToSignUpScreen : Navigation()
        data class NavigateToOtpScreen(val userId: Int, val verificationStep: String) : Navigation()
        data class NavigateToAllCharactersScreen(val userId: Int) : Navigation()
    }

    sealed interface LoginFlow : HomeEffect {
        data object ProceedWithLogin : LoginFlow
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefKeys: PrefKeys,
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor,
    private val registerInteractor: RegisterInteractor,
    private val authController: AuthController
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {
    override fun setInitialState(): HomeState =
        HomeState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null,
            isAlertDialogVisible = false,
            alertDialogTitle = "",
            alertDialogErrorMessage = "",
        )

    override fun handleEvents(event: HomeEvent) {
        when (event) {
            HomeEvent.InitView -> {
                initView()
            }

            HomeEvent.OnBack -> {
                setEffect {
                    HomeEffect.Navigation.PopBackStack
                }
            }

            HomeEvent.OnLogInButtonClick -> {
                onLogInButtonClick()
            }

            HomeEvent.OnSignUpButtonClick -> {
                onSignUpButtonClick()
            }

            HomeEvent.AlertDialog.OnOkClick -> {
                hideAlertDialog()
            }

            HomeEvent.OnProceedWithLogin -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                postLogin()
            }
        }
    }

    private fun initView() {
        // No stored user credentials; thus, do not proceed any further.
        if (!prefKeys.containsUserCredentials()) {
            setState {
                copy(
                    isLoading = false
                )
            }
            // todo: show error
            return
        }

        // Stored credentials were found, so proceed with login flow.
        setState {
            copy(
                isLoading = true
            )
        }
        loginFlow()
    }

    private fun loginFlow() {
        // Check if refresh token is expired.
        if (authController.isRefreshTokenExpired()) {
            // Refresh token is expired, so logout.
            logout()
            return
        }

        // Refresh access token.
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            userInteractor.refreshAccessToken().collect {
                when (it) {
                    is RefreshAccessTokenPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        // Could NOT refresh access token, so logout.
                        logout()
                        showAlertDialog(errorMessage = it.errorMessage)
                    }

                    is RefreshAccessTokenPartialState.Success -> {
                        // Proceed with login.
                        setEffect {
                            HomeEffect.LoginFlow.ProceedWithLogin
                        }
                    }
                }
            }
        }
    }

    private fun postLogin() {
        viewModelScope.launch {
            registerInteractor.postLoginWithToken(getUserLoginWithTokenRequest()).collect {
                when (it) {
                    is PostLoginWithTokenPartialState.Failed -> {
                        // An error occurred either due to an invalid token, or user id; proceed to remove the stored user credentials (userId, UserAuthData).
                        authController.removeUserCredentials()

                        setState {
                            copy(
                                isLoading = false
                            )
                        }

                        // Display erroneous alert dialog.
                        showAlertDialog(
                            errorMessage = it.errorMessage
                        )
                    }

                    is PostLoginWithTokenPartialState.Success -> {
                        it.loginWithTokenResponseDomain.verificationStep.let { verificationStep ->
                            when (verificationStep) {
                                is VerificationStep.None -> {
                                    // No verification step, so navigate to All Characters Screen.
                                    // There is no need to generate a new token as it has already
                                    // been generated in the `loginFlow` function.
                                    setEffect {
                                        HomeEffect.Navigation.NavigateToAllCharactersScreen(
                                            userId = prefKeys.getUserId()
                                        )
                                    }
                                }

                                is VerificationStep.OtpEmail -> {
                                    // Currently we only support OTP from email.
                                    setEffect {
                                        HomeEffect.Navigation.NavigateToOtpScreen(
                                            userId = prefKeys.getUserId(),
                                            verificationStep = verificationStep.name
                                        )
                                    }
                                }

                                is VerificationStep.OtpPhoneNumber -> {
                                    setState {
                                        copy(
                                            isLoading = false
                                        )
                                    }

                                    TODO("\"VerificationStep.OtpPhoneNumber\" not implemented yet")
                                }

                                is VerificationStep.Invalid -> {
                                    // An error occurred, so just in case, remove the stored user credentials (userId, UserAuthData).
                                    authController.removeUserCredentials()

                                    setState {
                                        copy(
                                            isLoading = false
                                        )
                                    }

                                    showAlertDialog(
                                        errorMessage = resourceProvider.getGenericErrorMessage()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            userInteractor.logout().collect {
                when (it) {
                    is LogoutPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                    }

                    LogoutPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onLogInButtonClick() {
        setEffect {
            HomeEffect.Navigation.NavigateToLogInScreen
        }
    }

    private fun onSignUpButtonClick() {
        setEffect {
            HomeEffect.Navigation.NavigateToSignUpScreen
        }
    }

    private fun getUserLoginWithTokenRequest(): UserLoginWithTokenRequest =
        UserLoginWithTokenRequest(
            userId = prefKeys.getUserId(),
            epoch = DateUtil.getSecondsFromEpoch()
        )

    private fun getAlertDialogTitle(): String =
        resourceProvider.getString(R.string.login_screen_alert_dialog_title)

    private fun showAlertDialog(errorMessage: String) {
        setState {
            copy(
                isAlertDialogVisible = true,
                alertDialogTitle = getAlertDialogTitle(),
                alertDialogErrorMessage = errorMessage
            )
        }
    }

    private fun hideAlertDialog() {
        setState {
            copy(
                isAlertDialogVisible = false,
                alertDialogTitle = "",
                alertDialogErrorMessage = ""
            )
        }
    }
}