package com.alexgreece45.marvelx.domain.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.UserLogInResponseDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.logIn.toDomain
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStatePostLogIn
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
import com.alexgreece45.marvelx.util.extensions.toMd5
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * The title of the Alert Dialog in [LoginScreen].
 * */
private const val ALERT_DIALOG_TITLE = "Login failed"

sealed class LogInEvent : BaseEvent {
    object InitView : LogInEvent()

    object OnPasswordVisibilityIconClick : LogInEvent()

    sealed class FieldValueChange : LogInEvent() {
        data class Email(val email: String) : FieldValueChange()

        data class Password(val password: String) : FieldValueChange()
    }

    object OnDoneClick : LogInEvent()

    object OnLogInButtonClick : LogInEvent()

    sealed class AlertDialog : LogInEvent() {
        object OnOkClick : AlertDialog()
    }

    object OnNoInternetRetryButtonClick : LogInEvent()
}

data class LogInState(
    // Base states.
    val isLoading: Boolean = false,

    // Data.
    val email: String = "",
    val password: String = "",

    /**
     * Controls whether the password is visible in the UI.
     * */
    val isPasswordVisible: Boolean = false,

    // SignUp button
    val logInButton: Boolean = false,

    // Alert Dialog.
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = ALERT_DIALOG_TITLE,
    val alertDialogErrorMessage: String = "",

    // Validations.
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),

    // User login successful response.
    val userLogInResponseDomain: UserLogInResponseDomain? = null,
) : BaseState {
    companion object {
        fun getDefaultValues(): LogInState = LogInState(
            isLoading = false,
            email = "",
            password = "",
            isPasswordVisible = false,
            logInButton = false,
            isAlertDialogVisible = false,
            alertDialogTitle = ALERT_DIALOG_TITLE,
            alertDialogErrorMessage = "",
            emailValidationResult = FormsValidationResult(isValid = false),
            passwordValidationResult = FormsValidationResult(isValid = false),
            userLogInResponseDomain = null
        )
    }
}

sealed class LogInEffect : BaseEffect {
    sealed class Navigation : LogInEffect() {
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
     * It should be used only in [LogInEvent.OnDoneClick],
     * and [LogInEvent.OnLogInButtonClick].
     * */
    object CloseKeyboardAndClearFocus : LogInEffect()
}

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val userInteractor: UserInteractor,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<LogInEvent, LogInState, LogInEffect>() {

    override fun setInitialState(): LogInState = LogInState.getDefaultValues()

    init {
        setEvent(LogInEvent.InitView)
    }

    override suspend fun handleEvents(event: LogInEvent) {
        when (event) {
            LogInEvent.InitView -> {
                initView()
            }

            is LogInEvent.FieldValueChange.Email -> {
                setState {
                    copy(
                        email = event.email
                    )
                }
                validateEmail(email = event.email)
            }

            is LogInEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password
                    )
                }
                validatePassword(password = event.password)
            }

            LogInEvent.OnDoneClick -> {
                if (currentState.logInButton) {
                    setEvent(
                        LogInEvent.OnLogInButtonClick
                    )
                }
            }

            LogInEvent.OnLogInButtonClick -> {
                onLogInButtonClick()
            }

            LogInEvent.OnPasswordVisibilityIconClick -> {
                setState {
                    copy(
                        isPasswordVisible = currentState.isPasswordVisible.not()
                    )
                }
            }

            LogInEvent.OnNoInternetRetryButtonClick -> {
                setEvent(
                    LogInEvent.InitView
                )
            }

            LogInEvent.AlertDialog.OnOkClick -> {
                hideAlertDialog()
            }
        }
    }

    private fun initView() {
        // Sets all states to their default values.
        setState {
            LogInState.getDefaultValues()
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
                            logInButton = formsValidationResult.isValid && areAllFieldsValid()
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
                            logInButton = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }
        }
    }

    private fun onLogInButtonClick() {
        // First of all, close the keyboard, and remove focus from all UI elements.
        setEffect {
            LogInEffect.CloseKeyboardAndClearFocus
        }

        // TODO: get user's ip address, and device info.

        // One or more fields are invalid. We don't know which one, so we disable the "LOGIN" button
        // and display an error message.
        if (!areAllFieldsValid()) {
            setState {
                LogInState.getDefaultValues().copy(
                    logInButton = false
                )
            }
            showAlertDialog(
                errorMessage = resourceProvider.getString(R.string.unknown_error)
            )
            return
        }

        // All fields are valid.
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            userInteractor.postLogIn(
                userLogInRequest = getUserLogInRequest()
            ).collect { userPartialStatePostLogIn ->
                when (userPartialStatePostLogIn) {
                    is UserPartialStatePostLogIn.Error -> {
                        setState {
                            LogInState.getDefaultValues().copy(
                                isLoading = false
                            )
                        }
                        showAlertDialog(
                            errorMessage = userPartialStatePostLogIn.errorMessage
                        )
                    }

                    UserPartialStatePostLogIn.NoInternetConnection -> {
                        setState {
                            LogInState.getDefaultValues().copy(
                                isLoading = false
                            )
                        }
                        showAlertDialog(
                            errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                        )
                    }

                    is UserPartialStatePostLogIn.Success -> {
                        setState {
                            LogInState.getDefaultValues().copy(
                                isLoading = false,
                                userLogInResponseDomain = userPartialStatePostLogIn.userLogInResponse.toDomain()
                            )
                        }

                        // Act accordingly based on the verification step.
                        currentState.userLogInResponseDomain?.let { userLogInResponseDomain ->
                            when (userLogInResponseDomain.verificationStep) {
                                is VerificationStep.None -> {
                                    // No verification step, so navigate to All Characters Screen.
                                    setEffect {
                                        LogInEffect.Navigation.NavigateToAllCharactersScreen(userId = 9999999)
                                    }
                                }

                                is VerificationStep.Invalid -> {
                                    showAlertDialog(
                                        errorMessage = resourceProvider.getString(R.string.unknown_error)
                                    )
                                }

                                VerificationStep.OtpEmail -> {
                                    // Currently we only support otp from email.
                                    setEffect {
                                        LogInEffect.Navigation.NavigateToOtpScreen(
                                            userId = currentState.userLogInResponseDomain!!.userId
                                        )
                                    }
                                }

                                VerificationStep.OtpPhoneNumber -> {
                                    // TODO: display appropriate screen
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

    private fun getUserLogInRequest(): UserLogInRequest =
        UserLogInRequest(
            email = currentState.email,
            password = SecurityUtil.getHashedPasswordFromString(currentState.password),
            epoch = DateUtil.getSecondsFromEpoch()
        )

    // TODO: TO BE REMOVED
    private fun createHashedPassword(password: String): String {
        // Store password.
        val editor = sharedPreferences.edit()
        editor.putString(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_PASSWORD, password)
        editor.apply()

        return (sharedPreferences.getString(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_PASSWORD,
            "unknown key: ${BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_PASSWORD}"
        ) ?: "unknown key: ${BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_PASSWORD}").toMd5()
    }

    private fun resetCredentialStates() {
        setState {
            copy(
                email = "",
                password = "",
                isPasswordVisible = false,
                emailValidationResult = FormsValidationResult(false),
                passwordValidationResult = FormsValidationResult(false)
            )
        }
    }

    private fun showAlertDialog(errorMessage: String) {
        setState {
            copy(
                isAlertDialogVisible = true,
                alertDialogTitle = ALERT_DIALOG_TITLE,
                alertDialogErrorMessage = errorMessage
            )
        }
    }

    private fun hideAlertDialog() {
        setState {
            copy(
                isAlertDialogVisible = false,
                alertDialogTitle = ALERT_DIALOG_TITLE,
                alertDialogErrorMessage = ""
            )
        }
    }
}