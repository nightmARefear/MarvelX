package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStatePostLoginWithToken
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeEvent : BaseEvent {
    object InitView : HomeEvent()

    object OnBack : HomeEvent()

    sealed class AlertDialog : HomeEvent() {
        object OnOkClick : AlertDialog()
    }

    object OnLogInButtonClick : HomeEvent()

    object OnSignUpButtonClick : HomeEvent()
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

sealed class HomeEffect : BaseEffect {
    sealed class Navigation : HomeEffect() {
        object PopBackStack : Navigation()
        object NavigateToLogInScreen : Navigation()
        object NavigateToSignUpScreen : Navigation()
        data class NavigateToOtpScreen(val userId: Int) : Navigation()
        data class NavigateToAllCharactersScreen(val userId: Int) : Navigation()
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesKeys: PreferencesKeys,
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor
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
                // Pop back stack.
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
        }
    }

    private fun initView() {
        // No stored user credentials; thus, do not proceed any further.
        if (!preferencesKeys.containsUserCredentials()) {
            setState {
                copy(
                    isLoading = false // Stop loading animation.
                )
            }
            return
        }

        /* A stored token was found.
        * Check if it is expired;
        * - if expired, refresh, and then proceed to login
        * - if NOT expired, validate it
        */
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            userInteractor.postLoginWithToken(getUserLoginWithTokenRequest()).collect {
                when (it) {
                    is UserPartialStatePostLoginWithToken.Error -> {
                        // An error occurred either due to an invalid token, or user id; proceed to remove the stored user credentials (userId, token).
                        preferencesKeys.removeUserCredentials()

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

                    is UserPartialStatePostLoginWithToken.Success -> {
                        when (it.userLoginWithTokenResponseDomain.verificationStep) {
                            is VerificationStep.None -> {
                                setState {
                                    copy(
//                                        isLoading = false // do not disable loading, so that the user cannot click on the Login/Signup buttons
                                    )
                                }

                                // Update stored token if needed.
                                it.userLoginWithTokenResponseDomain.token?.let { newToken ->
                                    preferencesKeys.setUserToken(newToken)
                                }

                                // No verification step, so navigate to AllCharactersScreen.
                                setEffect {
                                    HomeEffect.Navigation.NavigateToAllCharactersScreen(
                                        userId = preferencesKeys.getUserId()
                                    )
                                }
                            }

                            is VerificationStep.OtpEmail -> {
                                // Update stored token if needed.
                                it.userLoginWithTokenResponseDomain.token?.let { newToken ->
                                    preferencesKeys.setUserToken(newToken)
                                }

                                // Currently we only support OTP from email.
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }

                                setEffect {
                                    HomeEffect.Navigation.NavigateToOtpScreen(
                                        userId = preferencesKeys.getUserId()
                                    )
                                }
                            }

                            is VerificationStep.OtpPhoneNumber -> {
                                // Update stored token if needed.
                                it.userLoginWithTokenResponseDomain.token?.let { newToken ->
                                    preferencesKeys.setUserToken(newToken)
                                }

                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }

                                TODO("\"VerificationStep.OtpPhoneNumber\" not implemented yet")
                            }

                            is VerificationStep.Invalid -> {
                                // An error occurred, so just in case, remove the stored user credentials (userId, userToken).
                                preferencesKeys.removeUserCredentials()

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
            userId = preferencesKeys.getUserId(),
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