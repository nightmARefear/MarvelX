package com.alexgreece45.marvelx.ui.screens.introLoading

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LoginWithTokenPartialState
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.interactors.PostLoginPartialState
import com.alexgreece45.marvelx.domain.interactors.RefreshAccessTokenPartialState
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractor
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch

sealed interface IntroLoadingContract {
    /**
     * This sealed class contains all the possible loading stages. Each stage contains a [label]
     * that is displayed in the screen to inform the user in which loading stage the application is
     * at.
     *
     * @param label The string resource label describing this loading stage.
     */
    sealed class LoadingStage(@StringRes val label: Int) : IntroLoadingContract {
        data object RefreshAccessToken :
            LoadingStage(label = R.string.intro_loading_screen_label_loading_stage_refresh_access_token)

        data object LoginWithToken :
            LoadingStage(label = R.string.intro_loading_screen_label_loading_stage_post_login_with_token)
    }

    sealed interface Event : BaseEvent {
        data object Init : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val loadingStage: LoadingStage = LoadingStage.RefreshAccessToken
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data class NavigateToHomeScreenWithErrorMessage(val errorMessage: String? = null) :
                Navigation

            data class NavigateToOtpScreen(
                val userId: String,
                val otpFlowType: String,
                val verificationStep: String,
                val shouldRememberUserSession: Boolean
            ) : Navigation

            data object NavigateToAllCharactersScreen : Navigation
        }
    }
}

class IntroLoadingViewModel(
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys,
    private val registerInteractor: RegisterInteractor,
    private val loginInteractor: LoginInteractor,
    private val authController: AuthController,
    private val websocketController: WebsocketController,
    private val refreshTokenController: RefreshTokenController
) :
    BaseViewModel<IntroLoadingContract.Event, IntroLoadingContract.State, IntroLoadingContract.Effect>() {
    override fun setInitialState(): IntroLoadingContract.State =
        IntroLoadingContract.State()

    override fun handleEvents(event: IntroLoadingContract.Event) {
        when (event) {
            IntroLoadingContract.Event.Init -> {
                resetFailedAuthenticationValues()
                loginFlow()
            }
        }
    }

    /**
     * Resets the failed authentication related values.
     */
    private fun resetFailedAuthenticationValues() {
        authController.currentConsecutiveFailedAuthenticationData = null
    }

    /**
     * The login flow. It starts by checking if the user is eligible for requesting to be logged in,
     * and once that has been verified, their tokens are renewed. By using the newly generated
     * tokens, an attempt is made to login.
     */
    private fun loginFlow() {
        setState {
            copy(
                loadingStage = IntroLoadingContract.LoadingStage.RefreshAccessToken
            )
        }

        if (!prefKeys.isUserLoggedIn()) {
            setEffect {
                IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage()
            }
            return
        }

        if (!prefKeys.containsUserCredentials()) {
            setEffect {
                IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage()
            }
            // TODO: Show error.
            return
        }

        if (authController.isRefreshTokenExpired()) {
            logout()
            return
        }

        viewModelScope.launch {
            loginInteractor.refreshAccessToken().collect {
                when (it) {
                    is RefreshAccessTokenPartialState.Failed -> {
                        // Couldn't refresh access token, so logout.
                        logout()
                    }

                    is RefreshAccessTokenPartialState.Success -> {
                        // Proceed with login.
                        loginWithToken()
                    }
                }
            }
        }
    }

    /**
     * Logins the user with their token.
     */
    private fun loginWithToken() {
        /**
         * The erroneous actions to perform when [RegisterInteractor.loginWithToken] has failed,
         * or [VerificationStep.Invalid] was returned.
         */
        fun errorActions(errorMessage: String) {
            authController.removeUserCredentials()
            setEffect {
                IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage(
                    errorMessage = errorMessage
                )
            }
        }

        setState {
            copy(
                loadingStage = IntroLoadingContract.LoadingStage.LoginWithToken
            )
        }
        viewModelScope.launch {
            registerInteractor.loginWithToken().collect {
                when (it) {
                    // An error occurred either due to an invalid token, or userId.
                    is LoginWithTokenPartialState.Failed -> {
                        errorActions(it.errorMessage)
                    }

                    is LoginWithTokenPartialState.Success -> {
                        it.loginWithTokenResponseDomain.verificationStep.let { verificationStep ->
                            when (verificationStep) {
                                // No verification step, so navigate to `AllCharactersScreen`.
                                // There is no need to generate a new token as it has already
                                // been generated in the `loginFlow` function.
                                VerificationStep.None -> {
                                    // Retrieve sign data.
                                    loginInteractor.postLogin(authController.getAccessToken())
                                        .collect { postLoginPartialState ->
                                            when (postLoginPartialState) {
                                                PostLoginPartialState.Success -> {
                                                    refreshTokenController.startWorker()
                                                    websocketController.init()
                                                    prefKeys.autoLogoutTimestamp =
                                                        DateUtil.getUTCInstant()

                                                    setEffect {
                                                        IntroLoadingContract.Effect.Navigation.NavigateToAllCharactersScreen
                                                    }
                                                }

                                                is PostLoginPartialState.Failed -> {
                                                    errorActions(postLoginPartialState.errorMessage)
                                                }
                                            }
                                        }
                                }

                                VerificationStep.OtpEmail -> {
                                    setEffect {
                                        IntroLoadingContract.Effect.Navigation.NavigateToOtpScreen(
                                            userId = prefKeys.getUserId(),
                                            otpFlowType = OtpFlowType.Register.key,
                                            verificationStep = verificationStep.key,
                                            shouldRememberUserSession = prefKeys.shouldRememberUserSession()
                                        )
                                    }
                                }

                                // Currently we only support OTP from email.
                                VerificationStep.OtpPhoneNumber -> {
                                    TODO("\"VerificationStep.OtpPhoneNumber\" not implemented yet")
                                }

                                is VerificationStep.Invalid -> {
                                    errorActions(resourceProvider.getGenericErrorMessage())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Logouts the user.
     */
    private fun logout() {
        viewModelScope.launch {
            loginInteractor.logout().collect {
                when (it) {
                    is LogoutPartialState.Failed, LogoutPartialState.Success -> {
                        setEffect {
                            IntroLoadingContract.Effect.Navigation.NavigateToHomeScreenWithErrorMessage(
                                errorMessage = resourceProvider.getString(R.string.session_status_session_has_expired_alert_dialog_error_message)
                            )
                        }
                    }
                }
            }
        }
    }
}