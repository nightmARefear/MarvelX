package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.domains.otp.OtpFlowType
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.user.Gender
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.interactors.PostSignupPartialState
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

sealed class SignupEvent : BaseEvent {
    sealed class GenderDropdownMenu : SignupEvent() {
        data object OnClick : GenderDropdownMenu()
        data object OnDismiss : GenderDropdownMenu()
    }

    data object OnPasswordVisibilityIconClick : SignupEvent()

    sealed class FieldValueChange : SignupEvent() {
        data class FirstName(val firstName: String) : FieldValueChange()

        data class LastName(val lastName: String) : FieldValueChange()

        data class Gender(val gender: com.alexgreece45.marvelx.data.models.user.Gender) :
            FieldValueChange()

        data class Email(val email: String?) : FieldValueChange()

        data class Password(val password: String?) : FieldValueChange()
    }

    sealed class AlertDialogError : SignupEvent() {
        data class OnShow(val errorMessage: String) : AlertDialogError()
        data object OnOkClick : AlertDialogError()
    }

    data object OnDoneClick : SignupEvent()

    data object OnSignupButtonClick : SignupEvent()
}

data class SignupState(
    /**
     * Controls whether the loading animation is visible in the UI.
     */
    val isLoading: Boolean = false,

    // Data.
    val firstName: String = "",
    val lastName: String = "",
    val gender: Gender?,
    val email: String = "",
    val password: String = "",

    /**
     * Controls whether the gender dropdown menu is visible in the UI.
     * */
    val isGenderDropdownMenuVisible: Boolean = false,

    /**
     * Controls whether the password is visible in the UI.
     * */
    val isPasswordVisible: Boolean = false,

    /**
     * Controls whether the signup button is enabled in the UI.
     * */
    val signupButton: Boolean = false,

    // Alert Dialog.
    val isAlertDialogVisible: Boolean = false,
    val alertDialogTitle: String = "",
    val alertDialogErrorMessage: String = "",

    // UI fields' validations.
    val firstNameValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val lastNameValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val genderValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = false),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = false)
) : BaseState

sealed class SignupEffect : BaseEffect {
    sealed class Navigation : SignupEffect() {
        data object NavigateToAllCharactersScreen : Navigation()
        data class NavigateToOtpScreen(
            val userId: Int,
            val otpFlowType: String = OtpFlowType.Register.key,
            val verificationStep: String,
            val shouldRememberUserSession: Boolean
        ) : Navigation()
    }

    data class ShowAlertDialogError(val errorMessage: String) : SignupEffect()

    data object CloseKeyboardAndRemoveFocus : SignupEffect()

    data object OnDoneClick : SignupEffect()
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val registerInteractor: RegisterInteractor,
    private val prefKeys: PrefKeys
) : BaseViewModel<SignupEvent, SignupState, SignupEffect>() {

    override fun setInitialState(): SignupState =
        SignupState(
            isLoading = false,
            firstName = "",
            lastName = "",
            gender = null,
            email = "",
            password = "",
            isGenderDropdownMenuVisible = false,
            isPasswordVisible = false,
            signupButton = false,
            isAlertDialogVisible = false,
            alertDialogTitle = "",
            alertDialogErrorMessage = "",
            firstNameValidationResult = FormsValidationResult(isValid = false),
            lastNameValidationResult = FormsValidationResult(isValid = false),
            genderValidationResult = FormsValidationResult(isValid = false),
            emailValidationResult = FormsValidationResult(isValid = false),
            passwordValidationResult = FormsValidationResult(isValid = false)
        )

    override fun handleEvents(event: SignupEvent) {
        when (event) {
            is SignupEvent.FieldValueChange.FirstName -> {
                setState {
                    copy(
                        firstName = event.firstName
                    )
                }
                validateFirstName(firstName = event.firstName)
            }

            is SignupEvent.FieldValueChange.LastName -> {
                setState {
                    copy(
                        lastName = event.lastName
                    )
                }
                validateLastName(lastName = event.lastName)
            }

            is SignupEvent.FieldValueChange.Gender -> {
                setState {
                    copy(
                        gender = event.gender,
                        isGenderDropdownMenuVisible = false
                    )
                }
                validateGender(gender = event.gender)
            }

            is SignupEvent.FieldValueChange.Email -> {
                setState {
                    copy(
                        email = event.email.orEmpty()
                    )
                }
                validateEmail(email = event.email)
            }

            is SignupEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password.orEmpty()
                    )
                }
                validatePassword(password = event.password)
            }

            SignupEvent.OnSignupButtonClick -> {
                onSignUpButtonClick()
            }

            SignupEvent.OnPasswordVisibilityIconClick -> {
                setState {
                    copy(
                        isPasswordVisible = currentState.isPasswordVisible.not()
                    )
                }
            }

            is SignupEvent.AlertDialogError.OnShow -> {
                showAlertDialog(errorMessage = event.errorMessage)
            }

            SignupEvent.AlertDialogError.OnOkClick -> {
                hideAlertDialog()
            }

            SignupEvent.OnDoneClick -> {
                if (currentState.signupButton) {
                    setEffect {
                        SignupEffect.OnDoneClick
                    }
                }
            }

            SignupEvent.GenderDropdownMenu.OnClick -> {
                setState {
                    copy(
                        isGenderDropdownMenuVisible = !currentState.isGenderDropdownMenuVisible
                    )
                }
            }

            SignupEvent.GenderDropdownMenu.OnDismiss -> {
                setState {
                    copy(
                        isGenderDropdownMenuVisible = false
                    )
                }
            }
        }
    }

    private fun validateFirstName(firstName: String) {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getFirstNameValidationRules() to firstName))))
                .collect { formsValidationResult ->
                    setState {
                        copy(
                            firstNameValidationResult = formsValidationResult
                        )
                    }

                    setState {
                        copy(
                            signupButton = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }
        }
    }

    private fun validateLastName(lastName: String) {
        viewModelScope.launch {
            formValidator.validateForms(listOf(Form(mapOf(getLastNameValidationRules() to lastName))))
                .collect { formsValidationResult ->
                    setState {
                        copy(
                            lastNameValidationResult = formsValidationResult
                        )
                    }

                    setState {
                        copy(
                            signupButton = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }

        }
    }

    private fun validateGender(gender: Gender?) {
        gender?.let {
            viewModelScope.launch {
                formValidator.validateForms(listOf(Form(mapOf(getGenderValidationRules() to gender.key))))
                    .collect { formsValidationResult ->
                        setState {
                            copy(
                                genderValidationResult = formsValidationResult
                            )
                        }

                        setState {
                            copy(
                                signupButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun validateEmail(email: String?) {
        email?.let {
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
                                signupButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun validatePassword(password: String?) {
        password?.let {
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
                                signupButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun onSignUpButtonClick() {
        // First of all, close the keyboard and remove focus from the last field.
        setEffect {
            SignupEffect.CloseKeyboardAndRemoveFocus
        }

        // Check if all the fields are valid.
        if (areAllFieldsValid()) {
            setState {
                copy(
                    isLoading = true
                )
            }

            // TODO: get user's ip address, and device info.

            viewModelScope.launch {
                registerInteractor.postSignup(userSignupRequest = getUserSignupRequest())
                    .collect { userPartialStatePostSignUp ->
                        when (userPartialStatePostSignUp) {
                            is PostSignupPartialState.Failed -> {
                                resetUiFieldStates()

                                setEffect {
                                    SignupEffect.ShowAlertDialogError(
                                        errorMessage = userPartialStatePostSignUp.errorMessage
                                    )
                                }
                            }

                            is PostSignupPartialState.Success -> {
                                resetUiFieldStates()

                                userPartialStatePostSignUp.signupResponseDomain.let { signupResponseDomain ->
                                    // Act accordingly based on the verification step.
                                    when (signupResponseDomain.verificationStep) {
                                        // Server sent an invalid verification step type.
                                        is VerificationStep.Invalid -> {
                                            setEffect {
                                                SignupEffect.ShowAlertDialogError(
                                                    errorMessage = resourceProvider.getGenericErrorMessage()
                                                )
                                            }
                                        }

                                        VerificationStep.None -> {
                                            // User is logged in.
                                            prefKeys.setUserLoggedIn(isLoggedIn = true)

                                            // ONLY for signup, set the remember user session to `true` by default.
                                            prefKeys.setRememberUserSession(shouldRemember = true)

                                            setEffect {
                                                SignupEffect.Navigation.NavigateToAllCharactersScreen
                                            }
                                        }

                                        VerificationStep.OtpEmail -> {
                                            setEffect {
                                                SignupEffect.Navigation.NavigateToOtpScreen(
                                                    userId = signupResponseDomain.userId,
                                                    verificationStep = signupResponseDomain.verificationStep.key,
                                                    shouldRememberUserSession = true
                                                )
                                            }
                                        }

                                        VerificationStep.OtpPhoneNumber -> {
                                            setEffect {
                                                SignupEffect.Navigation.NavigateToOtpScreen(
                                                    userId = signupResponseDomain.userId,
                                                    verificationStep = signupResponseDomain.verificationStep.key,
                                                    shouldRememberUserSession = true
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        } else {
            // One or more fields are invalid, we don't know which one, so we reset
            // all the UI field states and display an unexpected error message.
            resetUiFieldStates()
            setEffect {
                SignupEffect.ShowAlertDialogError(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    private fun areAllFieldsValid(): Boolean {
        if (currentState.firstName.isBlank()) return false
        if (currentState.lastName.isBlank()) return false
        if (currentState.gender == null) return false
        if (currentState.email.isBlank()) return false
        if (currentState.password.isBlank()) return false

        if (!currentState.firstNameValidationResult.isValid) return false
        if (!currentState.lastNameValidationResult.isValid) return false
        if (!currentState.genderValidationResult.isValid) return false
        if (!currentState.emailValidationResult.isValid) return false
        if (!currentState.passwordValidationResult.isValid) return false

        return true
    }

    private fun getFirstNameValidationRules(): List<Rule> {
        val minLength = 2
        val maxLength = 50
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_blank_first_name)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_min_length_first_name,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_max_length_first_name,
                    maxLength
                )
            )
        )
    }

    private fun getLastNameValidationRules(): List<Rule> {
        val minLength = 2
        val maxLength = 50
        return listOf(
            Rule.ValidateNotBlank(errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_blank_last_name)),
            Rule.ValidateStringMinLength(
                length = minLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_min_length_last_name,
                    minLength
                )
            ),
            Rule.ValidateStringMaxLength(
                length = maxLength,
                errorMessage = resourceProvider.getString(
                    R.string.sign_up_screen_input_field_error_max_length_last_name,
                    maxLength
                )
            )
        )
    }

    private fun getGenderValidationRules(): List<Rule> =
        listOf(
            Rule.ValidateGender(
                errorMessage = resourceProvider.getString(R.string.sign_up_screen_input_field_error_blank_gender)
            )
        )

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

    private suspend fun getUserSignupRequest(): UserSignupRequest =
        UserSignupRequest(
            firstName = currentState.firstName,
            lastName = currentState.lastName,
            gender = currentState.gender?.key.orEmpty(),
            email = currentState.email,
            password = SecurityUtil.getHashedPasswordFromString(currentState.password).single()
        )

    /**
     * Returns the title of the Alert Dialog Error in `SignupScreen`.
     */
    private fun getAlertDialogTitle(): String =
        resourceProvider.getString(R.string.signup_screen_alert_dialog_title)

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
     * Resets all the UI field related states and disabled the loading animation.
     *
     * ### Note: This method MUST only be used when an error has occurred.
     */
    private fun resetUiFieldStates() {
        setState {
            copy(
                isLoading = false,
                firstName = "",
                lastName = "",
                gender = null,
                email = "",
                password = "",
                isGenderDropdownMenuVisible = false,
                isPasswordVisible = false,
                signupButton = false,
                firstNameValidationResult = FormsValidationResult(isValid = false),
                lastNameValidationResult = FormsValidationResult(isValid = false),
                genderValidationResult = FormsValidationResult(isValid = false),
                emailValidationResult = FormsValidationResult(isValid = false),
                passwordValidationResult = FormsValidationResult(isValid = false)
            )
        }
    }
}