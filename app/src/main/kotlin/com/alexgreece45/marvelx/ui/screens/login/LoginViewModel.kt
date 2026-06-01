package com.alexgreece45.marvelx.ui.screens.login

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.dispatchers.model.DefaultDispatcher
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.domain.interactors.LoginPartialState
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractor
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.login.model.LoginWithIntentWithGoogleResult
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.SecurityUtil
import com.alexgreece45.marvelx.util.constants.ConstantsInputField
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.coroutines.CoroutineContext

data class LoginScreenErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val onCloseClicked: (() -> Unit)? = null,
    val errorButtonLayout: ErrorButtonLayout.Single
)

sealed class LoginEvent : BaseEvent {
    data object Init : LoginEvent()

    data object OnBack : LoginEvent()

    sealed class FieldValueChange : LoginEvent() {
        data class Email(val email: String) : FieldValueChange()
        data class Password(val password: String) : FieldValueChange()
    }

    data object OnPasswordVisibilityIconClick : LoginEvent()

    data object OnRememberMeCheckboxClick : LoginEvent()

    data object OnForgotPasswordButtonClick : LoginEvent()

    data object OnDoneClick : LoginEvent()

    data object OnLoginButtonClick : LoginEvent()

    data class OnLoginWithGoogleResult(val loginWithIntentWithGoogleResult: LoginWithIntentWithGoogleResult) :
        LoginEvent()

    data object OnSignupTextClicked : LoginEvent()
}

data class LoginState(
    // Base states.
    val isLoading: Boolean = false,

    // Data.
    val email: String = "",
    val password: String = "",

    /**
     * Controls whether the password is visible in the UI.
     * */
    val isPasswordVisible: Boolean = false,

    val isRememberMeCheckboxChecked: Boolean = false,

    /**
     * Controls whether the login button is enabled.
     * */
    val isLoginButtonEnabled: Boolean = false,

    // Alert Dialog.
    val error: LoginScreenErrorView? = null,
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = "",
    val alertDialogErrorMessage: String = "",

    // Validations.
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = false)
) : BaseState

sealed class LoginEffect : BaseEffect {
    sealed class Navigation : LoginEffect() {
        data object PopBackStack : Navigation()

        data object NavigateToForgotPasswordScreen : Navigation()

        data class NavigateToAllCharactersScreen(val userId: String) : Navigation()

        data class NavigateToOtpScreen(
            val userId: String,
            val otpFlowType: String,
            val verificationStep: String,
            val shouldRememberUserSession: Boolean
        ) : Navigation()

        data object NavigateToSignupScreen : Navigation()
    }

    /**
     * ### This effect does two things:
     *
     * - Closes the keyboard.
     *
     * - Clears the focus from the last focused UI element.
     * This essentially clears the focus from all UI
     * elements, since the focus only applies to one
     * UI element at a time.
     *
     * It should be used only in [LoginEvent.OnDoneClick],
     * and [LoginEvent.OnLoginButtonClick].
     */
    data object CloseKeyboardAndClearFocus : LoginEffect()

    data class ShowToast(val text: String) : LoginEffect()

    data object SaveAutoFilledData : LoginEffect()
}

@KoinViewModel
class LoginViewModel(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val registerInteractor: RegisterInteractor,
    private val userSessionRepository: UserSessionRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineContext,
) : BaseViewModel<LoginEvent, LoginState, LoginEffect>() {

    override fun setInitialState(): LoginState = LoginState()

    override fun handleEvents(event: LoginEvent) {
        when (event) {
            LoginEvent.Init -> {
                initView()
            }

            LoginEvent.OnBack -> {
                setEffect {
                    LoginEffect.Navigation.PopBackStack
                }
            }

            is LoginEvent.FieldValueChange.Email -> {
                setState {
                    copy(
                        email = event.email
                    )
                }
                validateEmail()
            }

            is LoginEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password
                    )
                }
                validatePassword()
            }

            LoginEvent.OnDoneClick -> {
                // Execute only when the login button is enabled,
                // so the functionality is the same (i.e., if an error
                // occurs, it will be because a field's validation was
                // incorrectly calculated, etc.)
                if (currentState.isLoginButtonEnabled) {
                    setEvent(LoginEvent.OnLoginButtonClick)
                }
            }

            LoginEvent.OnLoginButtonClick -> {
                onLoginButtonClick()
            }

            LoginEvent.OnSignupTextClicked -> {
                setEffect {
                    LoginEffect.Navigation.NavigateToSignupScreen
                }
            }

            LoginEvent.OnPasswordVisibilityIconClick -> {
                setState {
                    copy(
                        isPasswordVisible = !isPasswordVisible
                    )
                }
            }

            LoginEvent.OnForgotPasswordButtonClick -> {
                setEffect {
                    LoginEffect.Navigation.NavigateToForgotPasswordScreen
                }
            }

            LoginEvent.OnRememberMeCheckboxClick -> {
                setState {
                    copy(
                        isRememberMeCheckboxChecked = currentState.isRememberMeCheckboxChecked.not()
                    )
                }
            }

            is LoginEvent.OnLoginWithGoogleResult -> {
                onLoginWithGoogleResult(event.loginWithIntentWithGoogleResult)
            }
        }
    }

    private fun initView() {
        // Sets all states to their default values.
        setState {
            setInitialState()
        }
    }

    private fun validateEmail() {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getEmailValidationRules() to currentState.email))))
                .collect { formsValidationResult ->
                    setState {
                        copy(
                            emailValidationResult = formsValidationResult
                        )
                    }

                    setState {
                        copy(
                            isLoginButtonEnabled = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }
        }
    }

    private fun validatePassword() {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getPasswordValidationRules() to currentState.password))))
                .collect { formsValidationResult ->
                    setState {
                        copy(
                            passwordValidationResult = formsValidationResult
                        )
                    }

                    setState {
                        copy(
                            isLoginButtonEnabled = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }
        }
    }

    private fun onLoginButtonClick() {
        // First of all, close the keyboard, and remove focus from all UI elements.
        setEffect {
            LoginEffect.CloseKeyboardAndClearFocus
        }

        // Display loading animation to the screen.
        setState {
            copy(
                isLoading = true
            )
        }

        // TODO: get user's device info.

        // One or more fields are invalid. We don't know which one, so we reset
        // the UI field states and display an error message.
        if (!areAllFieldsValid()) {
            setState {
                copy(
                    isLoading = false
                )
            }

            resetUiFieldStates()

            showErrorView(
                errorMessage = resourceProvider.getGenericErrorMessage(),
                onClose = ::hideErrorView,
                primaryButtonLabel = resourceProvider.getString(R.string.login_screen_back_to_login_button),
                onPrimaryButtonClicked = ::hideErrorView
            )
            return
        }

        // All fields are valid. Proceed with user login.
        loginUser(isLoggedInWithFirebase = false)
    }

    /**
     * Logins the user.
     */
    private fun loginUser(isLoggedInWithFirebase: Boolean) {
        viewModelScope.launch {
            registerInteractor.login(
                email = currentState.email,
                password = if (isLoggedInWithFirebase) "firebase" else SecurityUtil.getHashedPasswordFromString(
                    defaultDispatcher = defaultDispatcher,
                    password = currentState.password
                ).single(),
                isLoggedInWithFirebase = isLoggedInWithFirebase
            ).collect { userPartialStatePostLogIn ->
                when (userPartialStatePostLogIn) {
                    is LoginPartialState.Failed -> {
                        showErrorView(
                            errorMessage = userPartialStatePostLogIn.errorMessage,
                            onClose = {
                                resetUiFieldStates()
                                hideErrorView()
                            },
                            primaryButtonLabel = resourceProvider.getString(R.string.login_screen_back_to_login_button),
                            onPrimaryButtonClicked = {
                                resetUiFieldStates()
                                hideErrorView()
                            }
                        )
                    }

                    is LoginPartialState.Success -> {
                        // Act accordingly based on the verification step.
                        userPartialStatePostLogIn.loginResponseDomain.let { loginResponseDomain ->
                            when (loginResponseDomain.verificationStep) {
                                is VerificationStep.Invalid, VerificationStep.None -> {
                                    resetUiFieldStates()
                                    showErrorView(
                                        errorMessage = resourceProvider.getGenericErrorMessage(),
                                        onClose = ::hideErrorView,
                                        onPrimaryButtonClicked = ::hideErrorView
                                    )
                                }

                                is VerificationStep.OtpEmail -> {
                                    // Save userId in device.
                                    userSessionRepository.setUserId(userPartialStatePostLogIn.loginResponseDomain.userId)

                                    val isRememberMeCheckboxChecked =
                                        currentState.isRememberMeCheckboxChecked
                                    resetUiFieldStates()

                                    // Currently we only support OTP from email.
                                    setEffect {
                                        LoginEffect.Navigation.NavigateToOtpScreen(
                                            userId = loginResponseDomain.userId,
                                            otpFlowType = OtpFlowType.Register.key,
                                            verificationStep = loginResponseDomain.verificationStep.key,
                                            shouldRememberUserSession = isRememberMeCheckboxChecked
                                        )
                                    }
                                }

                                is VerificationStep.OtpPhoneNumber -> { // Save userId in device.
//                                    prefKeys.setUserId(userPartialStatePostLogIn.loginResponseDomain.userId) // todo: uncomment when this option is resolved

                                    // TODO: display appropriate screen
                                    TODO("\"VerificationStep.OtpPhoneNumber\" not implemented yet")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Login with google account flow.
     */
    private fun onLoginWithGoogleResult(loginWithIntentWithGoogleResult: LoginWithIntentWithGoogleResult) {
        when (loginWithIntentWithGoogleResult) {
            is LoginWithIntentWithGoogleResult.Failed -> {
                val errorMessage =
                    if (loginWithIntentWithGoogleResult.errorMessage.contains("Caller has been temporarily blocked due to too many canceled sign-in prompts.")) {
                        resourceProvider.getString(R.string.login_screen_error_login_with_google_result_too_many_requests)
                    } else {
                        resourceProvider.getString(R.string.generic_error)
                    }
                setEffect {
                    LoginEffect.ShowToast(errorMessage)
                }
            }

            is LoginWithIntentWithGoogleResult.Success -> {
                if (loginWithIntentWithGoogleResult.googleUserDomain?.email == null) {
                    setEffect {
                        LoginEffect.ShowToast(resourceProvider.getString(R.string.generic_error))
                    }
                    return
                }

                // Proceed to login the user.
                setState {
                    copy(
                        email = loginWithIntentWithGoogleResult.googleUserDomain.email
                    )
                }
                loginUser(isLoggedInWithFirebase = true)
            }

            LoginWithIntentWithGoogleResult.UnknownError -> {
                setEffect {
                    LoginEffect.ShowToast(resourceProvider.getString(R.string.generic_error))
                }
            }

            LoginWithIntentWithGoogleResult.Cancelled -> {
                setEffect {
                    LoginEffect.ShowToast(resourceProvider.getString(R.string.login_screen_error_login_with_google_result_cancelled))
                }
            }
        }
    }

    private fun areAllFieldsValid(): Boolean {
        if (currentState.email.isBlank()) return false
        if (currentState.password.isBlank()) return false

        if (!currentState.emailValidationResult.isValid) return false
        if (!currentState.passwordValidationResult.isValid) return false

        return true
    }

    // TODO: Get rules from formvalidationrules.
    private fun getEmailValidationRules(): List<Rule> {
        return listOf(
            Rule.ValidateNotBlank(
                errorMessage = resourceProvider.getString(R.string.input_field_error_blank_email)
            ),
            Rule.ValidateStringMinLength(
                length = ConstantsInputField.Email.minLength,
                errorMessage = resourceProvider.getString(
                    R.string.input_field_error_min_length_email,
                    ConstantsInputField.Email.minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = ConstantsInputField.Email.maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.input_field_error_max_length_email,
                    ConstantsInputField.Email.maxLength
                )
            ),
            Rule.ValidateEmail(
                errorMessage = resourceProvider.getString(R.string.input_field_error_validate_email_email)
            )
        )
    }

    private fun getPasswordValidationRules(): List<Rule> {
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_password)),
            Rule.ValidateStringMinLength(
                length = ConstantsInputField.Password.minLength,
                errorMessage = resourceProvider.getString(
                    R.string.input_field_error_min_length_password,
                    ConstantsInputField.Password.minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = ConstantsInputField.Password.maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.input_field_error_max_length_password,
                    ConstantsInputField.Password.maxLength
                )
            )
        )
    }

    /**
     * Displays the error view. It has a default title so there's no need to set it.
     *
     * @param errorMessage              The error message to be displayed as the subtitle of the
     * error view.
     * @param onClose                   The action(s) to be invoked when the `close` button on the
     * toolbar is clicked. Set to `null` if you don't want a toolbar action button at all.
     * @param primaryButtonLabel        The label of the primary button. It has a default of `blank`.
     * If you wish to change the label, you ***must*** provide a value.
     * @param onPrimaryButtonClicked    The action(s) to be invoked when the `primary` button is
     * clicked.
     */
    private fun showErrorView(
        errorMessage: String,
        onClose: (() -> Unit)? = null,
        primaryButtonLabel: String = resourceProvider.getString(R.string.generic_retry_button),
        onPrimaryButtonClicked: () -> Unit
    ) {
        setState {
            copy(
                error = LoginScreenErrorView(
                    errorTitle = resourceProvider.getString(R.string.login_screen_error_view_title),
                    errorSubtitle = errorMessage,
                    onCloseClicked = onClose,
                    errorButtonLayout = ErrorButtonLayout.Single(
                        primaryButtonLabel = primaryButtonLabel,
                        onPrimaryButtonClicked = onPrimaryButtonClicked
                    )
                )
            )
        }
    }

    private fun hideErrorView() {
        setState {
            copy(
                error = null
            )
        }
    }

    /**
     * This method MUST only be used when an error occurs and it is necessary to reset the UI field
     * states.
     *
     * ### Note: This method _disables_ the loading animation.
     */
    private fun resetUiFieldStates() {
        setState {
            copy(
                isLoading = false,
                email = "",
                password = "",
                isPasswordVisible = false,
                isRememberMeCheckboxChecked = false,
                isLoginButtonEnabled = false,
                emailValidationResult = FormsValidationResult(isValid = false),
                passwordValidationResult = FormsValidationResult(isValid = false)
            )
        }
    }
}
