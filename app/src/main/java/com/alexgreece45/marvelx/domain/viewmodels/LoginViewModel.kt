package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.login.UserLoginResponseDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStatePostLogin
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.DateUtil
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
    object InitView : LoginEvent()

    object OnBack : LoginEvent()

    object OnPasswordVisibilityIconClick : LoginEvent()

    sealed class FieldValueChange : LoginEvent() {
        data class Email(val email: String) : FieldValueChange()

        data class Password(val password: String) : FieldValueChange()
    }

    object OnDoneClick : LoginEvent()

    object OnLoginButtonClick : LoginEvent()

    sealed class AlertDialog : LoginEvent() {
        object OnOkClick : AlertDialog()
    }

    object OnNoInternetRetryButtonClick : LoginEvent()
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

    /**
     * Controls whether the login button is enabled.
     * */
    val loginButton: Boolean = false,

    // Alert Dialog.
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = "",
    val alertDialogErrorMessage: String = "",

    // Validations.
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),

    // User login successful response.
    val userLoginResponseDomain: UserLoginResponseDomain? = null,
) : BaseState {
    companion object {
        fun getDefaultValues(): LoginState = LoginState(
            isLoading = false,
            email = "",
            password = "",
            isPasswordVisible = false,
            loginButton = false,
            isAlertDialogVisible = false,
            alertDialogTitle = "",
            alertDialogErrorMessage = "",
            emailValidationResult = FormsValidationResult(isValid = false),
            passwordValidationResult = FormsValidationResult(isValid = false),
            userLoginResponseDomain = null
        )
    }
}

sealed class LoginEffect : BaseEffect {
    sealed class Navigation : LoginEffect() {
        object PopBackStack : Navigation()

        data class NavigateToAllCharactersScreen(val userId: Int) : Navigation()

        data class NavigateToOtpScreen(val userId: Int) : Navigation()
    }

    /**
    ### This effect does two things:
     *
     * • Closes the keyboard.
     *
     * • Clears the focus from the last focused UI element.
     * This essentially clears the focus from all UI
     * elements, since the focus only applies to one
     * UI element at a time.
     *
     * It should be used only in [LoginEvent.OnDoneClick],
     * and [LoginEvent.OnLoginButtonClick].
     * */
    object CloseKeyboardAndClearFocus : LoginEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val userInteractor: UserInteractor,
    private val preferencesKeys: PreferencesKeys
) : BaseViewModel<LoginEvent, LoginState, LoginEffect>() {

    override fun setInitialState(): LoginState = LoginState.getDefaultValues()

    init {
        setEvent(LoginEvent.InitView)
    }

    override fun handleEvents(event: LoginEvent) {
        when (event) {
            LoginEvent.InitView -> {
                initView()
            }

            LoginEvent.OnBack -> {
                // Reset states just in case.
                setState {
                    LoginState.getDefaultValues()
                }
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
                if (currentState.loginButton) {
                    onLoginButtonClick()
                }
            }

            LoginEvent.OnLoginButtonClick -> {
                onLoginButtonClick()
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
        }
    }

    private fun initView() {
        // Sets all states to their default values.
        setState {
            LoginState.getDefaultValues()
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
                            loginButton = formsValidationResult.isValid && areAllFieldsValid()
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
                            loginButton = formsValidationResult.isValid && areAllFieldsValid()
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

        // One or more fields are invalid. We don't know which one, so we disable the "LOGIN" button
        // and display an error message.
        if (!areAllFieldsValid()) {
            setState {
                LoginState.getDefaultValues().copy(
                    isLoading = false,
                    loginButton = false
                )
            }
            showAlertDialog(
                errorMessage = resourceProvider.getString(R.string.unknown_error)
            )
            return
        }

        // All fields are valid.
        viewModelScope.launch {
            userInteractor.postLogin(
                userLogInRequest = getUserLogInRequest()
            ).collect { userPartialStatePostLogIn ->
                when (userPartialStatePostLogIn) {
                    is UserPartialStatePostLogin.Error -> {
                        setState {
                            LoginState.getDefaultValues().copy(
                                isLoading = false
                            )
                        }
                        showAlertDialog(
                            errorMessage = userPartialStatePostLogIn.errorMessage
                        )
                    }

                    is UserPartialStatePostLogin.Success -> {
                        setState {
                            LoginState.getDefaultValues().copy(
                                isLoading = false,
                                userLoginResponseDomain = userPartialStatePostLogIn.userLoginResponseDomain
                            )
                        }

                        // Act accordingly based on the verification step.
                        currentState.userLoginResponseDomain?.let { userLogInResponseDomain ->
                            when (userLogInResponseDomain.verificationStep) {
                                is VerificationStep.None -> {
                                    // Store userToken.
                                    userLogInResponseDomain.accessToken?.let { userToken ->
                                        preferencesKeys.setUserToken(
                                            userToken = userToken
                                        )
                                    }

                                    // Store userId.
                                    preferencesKeys.setUserId(userLogInResponseDomain.userId)

                                    // No verification step, so navigate to All Characters Screen.
                                    setEffect {
                                        LoginEffect.Navigation.NavigateToAllCharactersScreen(userId = userLogInResponseDomain.userId)
                                    }
                                }

                                is VerificationStep.Invalid -> {
                                    showAlertDialog(
                                        errorMessage = resourceProvider.getGenericErrorMessage()
                                    )
                                }

                                is VerificationStep.OtpEmail -> {
                                    // Currently we only support OTP from email.
                                    setEffect {
                                        LoginEffect.Navigation.NavigateToOtpScreen(
                                            userId = currentState.userLoginResponseDomain!!.userId
                                        )
                                    }
                                }

                                is VerificationStep.OtpPhoneNumber -> {
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

    private suspend fun getUserLogInRequest(): UserLoginRequest =
        UserLoginRequest(
            email = currentState.email,
            password = SecurityUtil.getHashedPasswordFromString(currentState.password).single(),
            epoch = DateUtil.getSecondsFromEpoch()
        )

    /**
     * Returns the title of the Alert Dialog in [com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen.LogInScreen].
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
}