package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.interactors.PostLoginPartialState
import com.alexgreece45.marvelx.interactors.RegisterInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.SecurityUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginEvent : BaseEvent {
    data object InitView : LoginEvent()

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

    data object OnSignupTextClicked : LoginEvent()

    sealed class AlertDialog : LoginEvent() {
        data object OnOkClick : AlertDialog()
    }

    data class OnShowErrorDialog(val errorMessage: String) : LoginEvent()

    data object OnNoInternetRetryButtonClick : LoginEvent()
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

        data class NavigateToAllCharactersScreen(val userId: Int) : Navigation()

        data class NavigateToOtpScreen(
            val userId: Int,
            val verificationStep: String,
            val shouldRememberUserSession: Boolean
        ) : Navigation()

        data object PopUpToSignupScreen : Navigation()
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

    data class ShowErrorDialog(val errorMessage: String) : LoginEffect()

    data object CloseKeyboardAndClearFocus : LoginEffect()

    data object OnDoneClick : LoginEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val registerInteractor: RegisterInteractor,
    private val prefKeys: PrefKeys
) : BaseViewModel<LoginEvent, LoginState, LoginEffect>() {

    override fun setInitialState(): LoginState =
        LoginState(
            isLoading = false,
            email = "",
            password = "",
            isPasswordVisible = false,
            isLoginButtonEnabled = false,
            isAlertDialogVisible = false,
            alertDialogTitle = "",
            alertDialogErrorMessage = "",
            emailValidationResult = FormsValidationResult(isValid = false),
            passwordValidationResult = FormsValidationResult(isValid = false)
        )

    override fun handleEvents(event: LoginEvent) {
        when (event) {
            LoginEvent.InitView -> {
                initView()
            }

            LoginEvent.OnBack -> {
                // Reset states just in case.
                resetUiFieldStates()
                // Pop back stack.
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
                validateEmail(email = event.email)
            }

            is LoginEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password
                    )
                }
                validatePassword(password = event.password)
            }

            LoginEvent.OnDoneClick -> {
                // Execute only when the login button is enabled,
                // so the functionality is the same (i.e., if an error
                // occurs, it will be because a field's validation was
                // incorrectly calculated, etc.)
                if (currentState.isLoginButtonEnabled) {
                    setEffect {
                        LoginEffect.OnDoneClick
                    }
                }
            }

            LoginEvent.OnLoginButtonClick -> {
                onLoginButtonClick()
            }

            LoginEvent.OnSignupTextClicked -> {
                setEffect {
                    LoginEffect.Navigation.PopUpToSignupScreen
                }
            }

            LoginEvent.OnPasswordVisibilityIconClick -> {
                setState {
                    copy(
                        isPasswordVisible = currentState.isPasswordVisible.not()
                    )
                }
            }

            LoginEvent.OnNoInternetRetryButtonClick -> {
                setEvent(
                    LoginEvent.InitView
                )
            }

            LoginEvent.AlertDialog.OnOkClick -> {
                hideAlertDialog()
            }

            is LoginEvent.OnShowErrorDialog -> {
                showAlertDialog(errorMessage = event.errorMessage)
            }

            LoginEvent.OnForgotPasswordButtonClick -> {

            }

            LoginEvent.OnRememberMeCheckboxClick -> {
                setState {
                    copy(
                        isRememberMeCheckboxChecked = currentState.isRememberMeCheckboxChecked.not()
                    )
                }
            }
        }
    }

    private fun initView() {
        // Sets all states to their default values.
        setState {
            setInitialState()
        }
    }

    private fun validateEmail(email: String) {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getEmailValidationRules() to email))))
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

    private fun validatePassword(password: String) {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getPasswordValidationRules() to password))))
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

        // TODO: get user's ip address, and device info.

        // One or more fields are invalid. We don't know which one, so we reset
        // the UI field states and display an error message.
        if (!areAllFieldsValid()) {
            setState {
                copy(
                    isLoading = false
                )
            }
            setEffect {
                LoginEffect.ShowErrorDialog(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
            return
        }

        // All fields are valid.
        viewModelScope.launch {
            registerInteractor.postLogin(
                email = currentState.email,
                password = SecurityUtil.getHashedPasswordFromString(currentState.password).single()
            ).collect { userPartialStatePostLogIn ->
                when (userPartialStatePostLogIn) {
                    is PostLoginPartialState.Failed -> {
                        resetUiFieldStates()
                        setEffect {
                            LoginEffect.ShowErrorDialog(
                                errorMessage = userPartialStatePostLogIn.errorMessage
                            )
                        }
                    }

                    is PostLoginPartialState.Success -> {
                        // Act accordingly based on the verification step.
                        userPartialStatePostLogIn.loginResponseDomain.let { loginResponseDomain ->
                            when (loginResponseDomain.verificationStep) {
                                is VerificationStep.None -> {
                                    // Save userId in device.
                                    prefKeys.setUserId(userPartialStatePostLogIn.loginResponseDomain.userId)

                                    // No verification step, thus user is logged in.
                                    prefKeys.setUserLoggedIn(isLoggedIn = true)

                                    // Save "Remember Me" in device since we're logged in.
                                    prefKeys.setRememberUserSession(shouldRemember = currentState.isRememberMeCheckboxChecked)

                                    resetUiFieldStates()

                                    // No verification step, so navigate to All Characters Screen.
                                    setEffect {
                                        LoginEffect.Navigation.NavigateToAllCharactersScreen(userId = loginResponseDomain.userId)
                                    }
                                }

                                is VerificationStep.Invalid -> {
                                    resetUiFieldStates()
                                    setEffect {
                                        LoginEffect.ShowErrorDialog(
                                            errorMessage = resourceProvider.getGenericErrorMessage()
                                        )
                                    }
                                }

                                is VerificationStep.OtpEmail -> {
                                    // Save userId in device.
                                    prefKeys.setUserId(userPartialStatePostLogIn.loginResponseDomain.userId)

                                    val isRememberMeCheckboxChecked = currentState.isRememberMeCheckboxChecked
                                    resetUiFieldStates()

                                    // Currently we only support OTP from email.
                                    setEffect {
                                        LoginEffect.Navigation.NavigateToOtpScreen(
                                            userId = loginResponseDomain.userId,
                                            verificationStep = loginResponseDomain.verificationStep.name,
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

    private fun areAllFieldsValid(): Boolean {
        if (currentState.email.isBlank()) return false
        if (currentState.password.isBlank()) return false

        if (!currentState.emailValidationResult.isValid) return false
        if (!currentState.passwordValidationResult.isValid) return false

        return true
    }

    private fun getEmailValidationRules(): List<Rule> {
        val minLength = 5
        val maxLength = 100
        return listOf(
            Rule.ValidateNotBlank(
                errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_blank_email)
            ),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_min_length_email,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_max_length_email,
                    maxLength
                )
            ),
            Rule.ValidateEmail(
                errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_validate_email_email)
            )
        )
    }

    private fun getPasswordValidationRules(): List<Rule> {
        val minLength = 10
        val maxLength = 30
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_blank_password)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_min_length_password,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_max_length_password,
                    maxLength
                )
            )
        )
    }

    /**
     * Returns the title of the Alert Dialog in `LoginScreen`.
     */
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

    /**
     * This method MUST only be used when an error occurs and it is
     * necessary to reset the UI field states.
     *
     * ### Note: this method, also, DISABLES the loading animation.
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