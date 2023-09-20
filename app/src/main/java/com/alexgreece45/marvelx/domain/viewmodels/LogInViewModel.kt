package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.user.UserLogInResponseDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.toDomain
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStatePostLogIn
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LogInEvent : BaseEvent {
    object InitView : LogInEvent()

    object OnPasswordVisibilityIconClick : LogInEvent()

    sealed class FieldValueChange : LogInEvent() {
        data class Email(val email: String) : FieldValueChange()

        data class Password(val password: String) : FieldValueChange()
    }

    object OnLogInButtonClick : LogInEvent()
}

data class LogInState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val email: String = "",
    val password: String = "",

    /**
     * Controls whether the password is visible in the UI.
     * */
    val isPasswordVisible: Boolean = false,

    // SignUp button
    val logInButton: Boolean = false,

    // Validations.
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),

    // User login response.
    val userLogInResponseDomain: UserLogInResponseDomain? = null
) : BaseState {
    companion object {
        fun getDefaultValues(): LogInState = LogInState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null,
            email = "",
            password = "",
            isPasswordVisible = false,
            logInButton = false,
            emailValidationResult = FormsValidationResult(isValid = true),
            passwordValidationResult = FormsValidationResult(isValid = true),
            userLogInResponseDomain = null
        )
    }
}

sealed class LogInEffect : BaseEffect {
    object NavigateToAllCharactersScreen : LogInEffect()

    data class DisplayErrorToastMessage(val errorMessage: String) : LogInEffect()

    object CloseKeyboard : LogInEffect()
}

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val userInteractor: UserInteractor
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
        }
    }

    private fun initView() {
        // get user's ip address, and device info.
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
        // First of all, close the keyboard.
        setEffect {
            LogInEffect.CloseKeyboard
        }

        // Check if all the fields are valid.
        if (areAllFieldsValid()) {
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
                                copy(
                                    isLoading = false,
                                    errorMessage = userPartialStatePostLogIn.errorMessage
                                )
                            }
                            setEffect {
                                LogInEffect.DisplayErrorToastMessage(errorMessage = userPartialStatePostLogIn.errorMessage)
                            }
                        }

                        UserPartialStatePostLogIn.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    hasInternetConnection = false
                                )
                            }
                        }

                        is UserPartialStatePostLogIn.Success -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    userLogInResponseDomain = userPartialStatePostLogIn.userLogInResponse.toDomain()
                                )
                            }

                            // Acts accordingly based on the verification step.
                            currentState.userLogInResponseDomain?.let { userLogInResponseDomain ->
                                when (userLogInResponseDomain.verificationStep) {
                                    is VerificationStep.None -> {
                                        // No verification step, so navigate to All Characters Screen.
                                        setEffect {
                                            LogInEffect.NavigateToAllCharactersScreen
                                        }
                                    }

                                    is VerificationStep.Invalid -> {
                                        setEffect {
                                            LogInEffect.DisplayErrorToastMessage(
                                                errorMessage = resourceProvider.getString(R.string.unknown_error)
                                            )
                                        }
                                    }

                                    VerificationStep.PinEmail -> {
                                        // todo: display appropriate screen
                                    }

                                    VerificationStep.PinSms -> {
                                        // todo: display appropriate screen
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // One or more fields are invalid, we don't know which one, so we disable the "Sign Up" button
            // and display an unexpected error message.
            setState {
                copy(
                    logInButton = false
                )
            }
            setEffect {
                LogInEffect.DisplayErrorToastMessage(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
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
            password = createHashedPassword(currentState.password)
        )

    private fun createHashedPassword(password: String): String {
        return password
    }
}