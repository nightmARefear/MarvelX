package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.models.user.Gender
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpRequest
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserPartialStatePostSignUp
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.SecurityUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignUpEvent : BaseEvent {
    object InitView : SignUpEvent()

    object OnPasswordVisibilityIconClick : SignUpEvent()

    sealed class FieldValueChange : SignUpEvent() {
        data class FirstName(val firstName: String) : FieldValueChange()

        data class LastName(val lastName: String) : FieldValueChange()

        data class Gender(val gender: com.alexgreece45.marvelx.data.models.user.Gender) :
            FieldValueChange()

        data class Email(val email: String?) : FieldValueChange()

        data class Password(val password: String?) : FieldValueChange()
    }

    object OnSignUpButtonClick : SignUpEvent()
}

data class SignUpState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val firstName: String = "",
    val lastName: String = "",
    val gender: Gender? = Gender.Male, // todo: change to null
    val email: String = "",
    val password: String = "",

    /**
     * Controls whether the password is visible in the UI.
     * */
    val isPasswordVisible: Boolean = false,

    // SignUp button
    val signUpButton: Boolean = false,

    // Validations.
    val firstNameValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val lastNameValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val genderValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val emailValidationResult: FormsValidationResult = FormsValidationResult(isValid = true),
    val passwordValidationResult: FormsValidationResult = FormsValidationResult(isValid = true)
) : BaseState {
    companion object {
        fun getDefaultValues(): SignUpState = SignUpState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null
        )
    }
}

sealed class SignUpEffect : BaseEffect {
    object SignUpSuccess : SignUpEffect()

    data class DisplayErrorToastMessage(val errorMessage: String) : SignUpEffect()

    object CloseKeyboard : SignUpEffect()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val userInteractor: UserInteractor
) : BaseViewModel<SignUpEvent, SignUpState, SignUpEffect>() {

    override fun setInitialState(): SignUpState = SignUpState.getDefaultValues()

    init {
        setEvent(SignUpEvent.InitView)
    }

    override fun handleEvents(event: SignUpEvent) {
        when (event) {
            SignUpEvent.InitView -> {
                initView()
            }

            is SignUpEvent.FieldValueChange.FirstName -> {
                setState {
                    copy(
                        firstName = event.firstName
                    )
                }
                validateFirstName(firstName = event.firstName)
            }

            is SignUpEvent.FieldValueChange.LastName -> {
                setState {
                    copy(
                        lastName = event.lastName
                    )
                }
                validateLastName(lastName = event.lastName)
            }

            is SignUpEvent.FieldValueChange.Gender -> {
                setState {
                    copy(
                        gender = event.gender
                    )
                }
                validateGender(gender = event.gender)
            }

            is SignUpEvent.FieldValueChange.Email -> {
                setState {
                    copy(
                        email = event.email.orEmpty()
                    )
                }
                validateEmail(email = event.email)
            }

            is SignUpEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password.orEmpty()
                    )
                }
                validatePassword(password = event.password)
            }

            SignUpEvent.OnSignUpButtonClick -> {
                onSignUpButtonClick()
            }

            SignUpEvent.OnPasswordVisibilityIconClick -> {
                setState {
                    copy(
                        isPasswordVisible = currentState.isPasswordVisible.not()
                    )
                }
            }
        }
    }

    private fun initView() {
        // todo: get user's ip address, and device info.
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
                            signUpButton = formsValidationResult.isValid && areAllFieldsValid()
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
                            signUpButton = formsValidationResult.isValid && areAllFieldsValid()
                        )
                    }
                }

        }
    }

    private fun validateGender(gender: Gender?) {
        gender?.let { _ ->
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
                                signUpButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun validateEmail(email: String?) {
        email?.let { _ ->
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
                                signUpButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun validatePassword(password: String?) {
        password?.let { _ ->
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
                                signUpButton = formsValidationResult.isValid && areAllFieldsValid()
                            )
                        }
                    }
            }
        }
    }

    private fun onSignUpButtonClick() {
        // First of all, close the keyboard.
        setEffect {
            SignUpEffect.CloseKeyboard
        }

        // Check if all the fields are valid.
        if (areAllFieldsValid()) {
            setState {
                copy(
                    isLoading = true
                )
            }
            viewModelScope.launch {
                userInteractor.postSignUp(
                    userSignUpRequest = getUserSignUpRequest()
                ).collect { userPartialStatePostSignUp ->
                    when (userPartialStatePostSignUp) {
                        is UserPartialStatePostSignUp.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = userPartialStatePostSignUp.errorMessage
                                )
                            }
                            setEffect {
                                SignUpEffect.DisplayErrorToastMessage(errorMessage = userPartialStatePostSignUp.errorMessage)
                            }
                        }

                        // TODO: REFACTOR
                       /* UserPartialStatePostSignUp.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    hasInternetConnection = false
                                )
                            }
                        }*/

                        is UserPartialStatePostSignUp.Success -> {
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                            // todo: change handling
                            setEffect {
                                SignUpEffect.SignUpSuccess
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
                    signUpButton = false
                )
            }
            setEffect {
                SignUpEffect.DisplayErrorToastMessage(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
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

    private suspend fun getUserSignUpRequest(): UserSignUpRequest =
        UserSignUpRequest(
            firstName = currentState.firstName,
            lastName = currentState.lastName,
            gender = currentState.gender?.key.orEmpty(),
            email = currentState.email,
            password = SecurityUtil.getHashedPasswordFromString(currentState.password).collect().toString()
        )
}