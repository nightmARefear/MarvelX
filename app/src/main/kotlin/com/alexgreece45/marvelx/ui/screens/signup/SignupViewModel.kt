package com.alexgreece45.marvelx.ui.screens.signup

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.OAuthAuthorizationController
import com.alexgreece45.marvelx.controllers.OAuthAuthorizationRequest
import com.alexgreece45.marvelx.controllers.OAuthCallbackResult
import com.alexgreece45.marvelx.controllers.OAuthLoginContext
import com.alexgreece45.marvelx.core.common.dispatchers.model.DefaultDispatcher
import com.alexgreece45.marvelx.core.network.model.api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractor
import com.alexgreece45.marvelx.domain.interactors.SignupPartialState
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.usecases.CompleteOAuthLoginResult
import com.alexgreece45.marvelx.domain.usecases.CompleteOAuthLoginUseCase
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.SecurityUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.coroutines.CoroutineContext

sealed class SignupEvent : BaseEvent {
    data object OnBack : SignupEvent()
    sealed class GenderDropdownMenu : SignupEvent() {
        data object OnClick : GenderDropdownMenu()
        data object OnDismiss : GenderDropdownMenu()
    }

    data object OnPasswordVisibilityIconClick : SignupEvent()

    sealed class FieldValueChange : SignupEvent() {
        data class FirstName(val firstName: String) : FieldValueChange()

        data class LastName(val lastName: String) : FieldValueChange()

        data class GenderDropDown(val gender: Gender) :
            FieldValueChange()

        data class Email(val email: String) : FieldValueChange()

        data class Password(val password: String) : FieldValueChange()
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
        data object GoBack : Navigation()
        data object NavigateToDashboardScreen : Navigation()
        data class NavigateToOtpScreen(
            val userId: String,
            val otpFlowType: String = OtpFlowType.Register.key,
            val verificationStep: String,
            val shouldRememberUserSession: Boolean
        ) : Navigation()
    }

    data class ShowAlertDialogError(val errorMessage: String) : SignupEffect()

    data object CloseKeyboardAndRemoveFocus : SignupEffect()

    data object OnDoneClick : SignupEffect()

    /**
     * Opens [request] (the OAuth 2.0 + PKCE authorization request) in the browser. The result of
     * the flow arrives asynchronously through `MainActivity`, not through this effect.
     */
    data class LaunchOAuthAuthorization(val request: OAuthAuthorizationRequest) : SignupEffect()
}

@KoinViewModel
internal class SignupViewModel(
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator,
    private val registerInteractor: RegisterInteractor,
    private val fieldValidationRules: FieldValidationRules,
    @DefaultDispatcher private val defaultDispatcher: CoroutineContext,
    private val oAuthAuthorizationController: OAuthAuthorizationController,
    private val completeOAuthLoginUseCase: CompleteOAuthLoginUseCase
) : BaseViewModel<SignupEvent, SignupState, SignupEffect>() {

    /**
     * Tracks the coroutine suspended in [OAuthAuthorizationController.awaitCallback] so a new
     * attempt replaces it, and it gets cancelled if the screen is torn down first.
     */
    private var oAuthCallbackJob: Job? = null

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
            SignupEvent.OnBack -> {
                if (currentState.isLoading) return

                setEffect { SignupEffect.Navigation.GoBack }
            }

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

            is SignupEvent.FieldValueChange.GenderDropDown -> {
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
                        email = event.email
                    )
                }
                validateEmail(email = event.email)
            }

            is SignupEvent.FieldValueChange.Password -> {
                setState {
                    copy(
                        password = event.password
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
            formValidator.validateForms(listOf(Form(mapOf(fieldValidationRules.getFirstNameValidationRules() to firstName))))
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
            formValidator.validateForms(listOf(Form(mapOf(fieldValidationRules.getLastNameValidationRules() to lastName))))
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
                formValidator.validateForms(listOf(Form(mapOf(fieldValidationRules.getGenderValidationRules() to gender.key))))
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
                formValidator.validateForms(listOf(Form(mapOf(fieldValidationRules.getEmailValidationRules() to email))))
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
                formValidator.validateForms(listOf(Form(mapOf(fieldValidationRules.getPasswordValidationRules() to password))))
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
                registerInteractor.signup(requestUserSignup = getUserSignupRequest())
                    .collect { userPartialStatePostSignUp ->
                        when (userPartialStatePostSignUp) {
                            is SignupPartialState.Failed -> {
                                resetUiFieldStates()

                                setEffect {
                                    SignupEffect.ShowAlertDialogError(
                                        errorMessage = userPartialStatePostSignUp.errorMessage
                                    )
                                }
                            }

                            is SignupPartialState.Success -> {
                                resetUiFieldStates()

                                userPartialStatePostSignUp.signupDomain.let { signupResponseDomain ->
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
                                            postSuccessfulSignup(
                                                userId = signupResponseDomain.userId,
                                                tempAuthCode = signupResponseDomain.tempAuthCode
                                            )
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

    /**
     * The operations to perform after successful signup, and when the [VerificationStep] is
     * [VerificationStep.None].
     *
     * It retrieves the user tokens, signing data, and logins the user.
     */
    private fun postSuccessfulSignup(userId: String, tempAuthCode: String) {
        // ONLY for signup, the user session is remembered by default.
        val loginContext = OAuthLoginContext(userId = userId, shouldRememberUserSession = true)

        oAuthCallbackJob?.cancel()
        oAuthCallbackJob = viewModelScope.launch {
            val request = oAuthAuthorizationController.startAuthorization(
                tempAuthCode = tempAuthCode,
                loginContext = loginContext
            )
            setEffect {
                SignupEffect.LaunchOAuthAuthorization(request)
            }

            when (val result = oAuthAuthorizationController.awaitCallback()) {
                is OAuthCallbackResult.Success -> {
                    completeLogin(authorization = result, loginContext = loginContext)
                }

                is OAuthCallbackResult.ServerError,
                OAuthCallbackResult.StateMismatch,
                OAuthCallbackResult.Timeout,
                OAuthCallbackResult.Cancelled -> {
                    setEffect {
                        SignupEffect.ShowAlertDialogError(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    }
                }
            }
        }
    }

    /**
     * Exchanges the authorization code for tokens and completes the login.
     */
    private suspend fun completeLogin(
        authorization: OAuthCallbackResult.Success,
        loginContext: OAuthLoginContext
    ) {
        val result = completeOAuthLoginUseCase(
            code = authorization.code,
            codeVerifier = authorization.codeVerifier,
            redirectUri = authorization.redirectUri,
            userId = loginContext.userId,
            shouldRememberUserSession = loginContext.shouldRememberUserSession
        )

        when (result) {
            CompleteOAuthLoginResult.Success -> {
                setEffect {
                    SignupEffect.Navigation.NavigateToDashboardScreen
                }
            }

            is CompleteOAuthLoginResult.TokenExchangeFailed,
            is CompleteOAuthLoginResult.PostLoginFailed -> {
                setEffect {
                    SignupEffect.ShowAlertDialogError(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
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

    private suspend fun getUserSignupRequest(): RequestUserSignup =
        RequestUserSignup(
            firstName = currentState.firstName,
            lastName = currentState.lastName,
            gender = currentState.gender?.key.orEmpty(),
            email = currentState.email,
            password = SecurityUtil.getHashedPasswordFromString(
                defaultDispatcher = defaultDispatcher,
                password = currentState.password
            ).single()
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