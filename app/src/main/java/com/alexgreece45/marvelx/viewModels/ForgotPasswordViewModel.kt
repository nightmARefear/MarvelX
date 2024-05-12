package com.alexgreece45.marvelx.viewModels

import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.controllers.TimerStatus
import com.alexgreece45.marvelx.data.domains.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.interactors.ForgotPasswordInteractor
import com.alexgreece45.marvelx.interactors.ForgotPasswordIsOtpBlockedEmailPartialState
import com.alexgreece45.marvelx.interactors.ForgotPasswordSendOtpPartialState
import com.alexgreece45.marvelx.interactors.ForgotPasswordValidateOtpPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.constants.ConstantsInputField
import com.alexgreece45.marvelx.viewModels.ForgotPasswordSubScreens.EnterEmail
import com.alexgreece45.marvelx.viewModels.ForgotPasswordSubScreens.PasswordChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Defines all the sub-screens needed for the `Forgot Password` flow. The string - related info
 * required by each sub-screen is provided.
 *
 * The initial sub-screen is ***always*** [EnterEmail], and the last [PasswordChanged].
 *
 * @param step                  The step value of the sub-screen represented as an [Int]. Defines
 * the order of the screen in the flow.
 * @param titleResId            The resource ID of the title of this sub-screen.
 * @param subtitleResId         The resource ID of the subtitle of this sub-screen.
 * @param stickyButtonResId     The resource ID of the sticky button of this sub-screen.
 */
sealed class ForgotPasswordSubScreens(
    val step: Int,
    @StringRes val titleResId: Int,
    @StringRes val subtitleResId: Int,
    @StringRes val stickyButtonResId: Int
) {
    companion object {
        /**
         * The first step of this flow.
         */
        val firstStep = EnterEmail

        /**
         * The final step of this flow. After it, there are no more screens.
         */
        val lastStep = PasswordChanged
    }

    /**
     * The first sub-screen of the `Forgot Password` flow. The user is informed about what this
     * screen does, and they enter the first piece of contact information; their email.
     */
    data object EnterEmail : ForgotPasswordSubScreens(
        step = 1,
        titleResId = R.string.forgot_password_enter_email_screen_title,
        subtitleResId = R.string.forgot_password_enter_email_screen_subtitle,
        stickyButtonResId = R.string.forgot_password_enter_email_screen_sticky_button
    )

    /**
     * The second sub-screen of the `Forgot Password` flow. The user enters the OTP that they have
     * received in their email and with the `requestIdEmail` from the previous sub-screen, the OTP
     * is validated.
     *
     * If another verification step is required, then the user in navigated to the next verification -
     * related sub-screen, otherwise to the final screen, which is [EnterNewPassword].
     */
    data object ValidateEmail : ForgotPasswordSubScreens(
        step = 2,
        titleResId = R.string.forgot_password_validate_email_screen_title,
        subtitleResId = R.string.forgot_password_validate_email_screen_subtitle,
        stickyButtonResId = R.string.forgot_password_validate_email_screen_sticky_button
    )

    data object EnterPhoneNumber : ForgotPasswordSubScreens(
        step = 3,
        titleResId = R.string.forgot_password_enter_phone_number_screen_title,
        subtitleResId = R.string.forgot_password_enter_phone_number_screen_subtitle,
        stickyButtonResId = R.string.forgot_password_enter_phone_number_screen_sticky_button
    )

    data object ValidatePhoneNumber : ForgotPasswordSubScreens(
        step = 4,
        titleResId = R.string.forgot_password_validate_phone_number_screen_title,
        subtitleResId = R.string.forgot_password_validate_phone_number_screen_subtitle,
        stickyButtonResId = R.string.forgot_password_validate_phone_number_screen_sticky_button
    )

    /**
     * The pre-last sub-screen of the `Forgot Password` flow. It combines the `requestIdEmail`,
     * `requestIdPhoneNumber` (if applicable) and the password that the user has entered in order to
     * reset the password.
     *
     * There is no next sub-screen. Upon successfully creating the new password, the user is
     * navigated to `LoginScreen`.
     */
    data object EnterNewPassword : ForgotPasswordSubScreens(
        step = 5,
        titleResId = R.string.change_password_screen_title,
        subtitleResId = R.string.change_password_screen_subtitle,
        stickyButtonResId = R.string.change_password_screen_button_sticky
    )

    /**
     * The last sub-screen of the `Forgot Password` flow. Based on the result from the
     * [EnterNewPassword] sub-screen, either a success or failed dialog appears.
     *
     * The user cannot navigate back from this screen using the system's back button. The only ways
     * to navigate away from this screen is either via clicking on the `BACK TO LOGIC` button, or
     * by closing and re-opening the application.
     */
    data object PasswordChanged : ForgotPasswordSubScreens(
        step = 6,
        titleResId = R.string.change_password_screen_title,
        subtitleResId = R.string.change_password_screen_subtitle,
        stickyButtonResId = R.string.change_password_screen_button_sticky
    )
}

sealed class ForgotPasswordEvent : BaseEvent {
    data class Init(
        val verificationStep: String?,
        val email: String?
    ) : ForgotPasswordEvent()

    data class OnEmailValueChange(val email: String) : ForgotPasswordEvent()

    data class OnOtpFieldValueChange(val otp: String) : ForgotPasswordEvent()

    data object OnBackClick : ForgotPasswordEvent()

    data object OnEmailDoneClick : ForgotPasswordEvent()

    data object OnDoneClick : ForgotPasswordEvent()

    data object OnContinueButtonClicked : ForgotPasswordEvent()

    data object OnResendOtpButtonClick : ForgotPasswordEvent()

    sealed class Navigation : ForgotPasswordEvent() {
        data object NavigateToValidateEmailSubScreen : Navigation()
    }
}

data class ForgotPasswordState(
    /**
     * Controls whether the loading animation is visible.
     */
    val isLoading: Boolean = false,

    /**
     * The current step of the `Forgot Password` flow.
     */
    val currentSubScreen: ForgotPasswordSubScreens = ForgotPasswordSubScreens.firstStep,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,

    /**
     * The verification step.
     */
    val verificationStep: VerificationStep = VerificationStep.Invalid(""),

    /**
     * The email that the user has entered.
     */
    val email: String = "",

    /**
     * The OTP that the user has entered.
     * */
    val otp: String = "",

    /**
     * The unique request ID of the email OTP.
     */
    val requestIdEmail: String = "",

    /**
     * The amount of remaining time, as a [String], until the OTP functionality is enabled so that
     * new OTPs can be requested. If set to `null`, the OTP functionality is enabled.
     */
    val otpBlockRemainingTime: String? = null,

    val otpFieldEnabled: Boolean = true,

    /**
     * Controls whether the `Continue` button is enabled. Its default value is `false`.
     */
    val isContinueButtonEnabled: Boolean = false,

    val isResendOtpButtonEnabled: Boolean = true,

    /**
     * Controls whether the verify button is enabled. When clicked, the user-entered OTP is verified.
     * This button must _only_ be enabled when the length of the OTP is 6. Also, when the OTP field
     * clears, this button should be disabled.
     */
    val isVerifyButtonEnabled: Boolean = false,

    // Validations.
    val emailValidationResult: FormValidationResult = FormValidationResult(isValid = false),
    val enteredOtpValidationResult: FormsValidationResult = FormsValidationResult(true)

) : BaseState

sealed class ForgotPasswordEffect : BaseEffect {
    data object OtpFieldRequestFocus : ForgotPasswordEffect()

    sealed class Navigation : ForgotPasswordEffect() {
        data object PopBackStack : Navigation()
        data object PopToLoginScreen : Navigation()
        data class NavigateToLoginScreenWithError(val errorMessage: String) : Navigation()

        // Sub-Screens.
        data object NavigateToValidateEmailSubScreen : Navigation()

        data class NavigateToOtpScreen(val requestIdEmail: String) : Navigation()
    }
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordInteractor: ForgotPasswordInteractor,
    private val resourceProvider: ResourceProvider,
    private val timerController: TimerController,
    private val formValidator: FormValidator
) : BaseViewModel<ForgotPasswordEvent, ForgotPasswordState, ForgotPasswordEffect>() {

    /**
     * A [Job] for the OTP countdown timer. Its initial value is set to `null` to represent that no
     * job is present. This job should be set to the respective parent [CoroutineScope] of the
     * caller of [TimerController.startCountdown], which usually is [viewModelScope].
     *
     * Before setting this job to a coroutine scope, make sure to cancel it by calling `cancel()`.
     * It is highly recommended to _**always**_ cancel this job before setting it to a new one.
     *
     * ## Example usage
     *
     * ### 1. Cancel this job.
     * ```
     * countdownJob?.cancel()
     * ```
     *
     * ### 2. Initialize this job.
     * ```
     * countdownJob = viewModelScope.launch {
     *                 timerController.startCountdown(duration = it.expiresIn)
     *                     .collect { timerStatus ->
     *                         when (timerStatus) {
     *                             TimerStatus.Completed -> {
     *                                 setState {
     *                                     copy(
     *                                         otpBlockRemainingTime = null,
     *                                         resendOtpButtonEnabled = true
     *                                     )
     *                                 }
     *                             }
     *
     *                             is TimerStatus.Ticking -> {
     *                                 setState {
     *                                     copy(
     *                                         otpBlockRemainingTime = timerStatus.remainingTime,
     *                                     )
     *                                 }
     *                             }
     *                         }
     *                     }
     *             }
     * ```
     */
    private var countdownJob: Job? = null

    override fun setInitialState(): ForgotPasswordState = ForgotPasswordState()

    override fun handleEvents(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.Init -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initValidateEmail(event.verificationStep, event.email)
            }

            is ForgotPasswordEvent.OnEmailValueChange -> {
                setState {
                    copy(
                        email = event.email
                    )
                }
                validateEmail()
            }

            is ForgotPasswordEvent.OnOtpFieldValueChange -> {
                if (event.otp != currentState.otp) {
                    onOtpFieldValueChange(otp = event.otp)
                }
            }

            ForgotPasswordEvent.OnEmailDoneClick -> {
                if (currentState.isContinueButtonEnabled) {
                    sendOtpToEmail()
                }
            }

            ForgotPasswordEvent.OnDoneClick -> {
                if (currentState.otpFieldEnabled) {
                    validateOtpEmail()
                }
            }

            ForgotPasswordEvent.OnBackClick -> {
                // Based on the current sub-screen, navigate the user appropriately.
                when (currentState.currentSubScreen) {
                    EnterEmail -> {
                        setEffect {
                            ForgotPasswordEffect.Navigation.PopToLoginScreen
                        }
                    }

                    ForgotPasswordSubScreens.ValidateEmail -> {
                        setState {
                            copy(
                                currentSubScreen = EnterEmail
                            )
                        }
                    }

                    ForgotPasswordSubScreens.EnterPhoneNumber -> TODO()
                    ForgotPasswordSubScreens.ValidatePhoneNumber -> TODO()
                    ForgotPasswordSubScreens.EnterNewPassword -> TODO()
                    PasswordChanged -> TODO()
                }
            }

            ForgotPasswordEvent.OnResendOtpButtonClick -> {
                resendOtpToEmail()
            }

            ForgotPasswordEvent.OnContinueButtonClicked -> {
                // Double check if the button is enabled.
                if (currentState.isContinueButtonEnabled) {
                    sendOtpToEmail()
                }
            }

            ForgotPasswordEvent.Navigation.NavigateToValidateEmailSubScreen -> {
//                setState {
//                    copy(
//                        currentSubScreen = ForgotPasswordSubScreens.ValidateEmail
//                    )
//                }
                // Navigate to OTP screen.
                setEffect {
                    ForgotPasswordEffect.Navigation.NavigateToOtpScreen(requestIdEmail = currentState.requestIdEmail)
                }
            }
        }
    }

    private fun initValidateEmail(verificationStep: String?, email: String?) {
        // Verification step && email are required.
        if (verificationStep == null || email == null) {
            setEffect {
                ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
            return
        }

        setState {
            copy(
                verificationStep = VerificationStep.of(verificationStep),
                email = email
            )
        }
        sendOtpToEmail()
    }

    /**
     * Sends an OTP to the provided email address.
     */
    private fun sendOtpToEmail() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            forgotPasswordInteractor.sendOtp(
                data = currentState.email,
                verificationStep = VerificationStep.OtpEmail
            ).collect {
                when (it) {
                    is ForgotPasswordSendOtpPartialState.Failed -> {
                        setEffect {
                            ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError(
                                errorMessage = resourceProvider.getGenericErrorMessage()
                            )
                        }
                        return@collect
                    }

                    is ForgotPasswordSendOtpPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                requestIdEmail = it.requestId
                            )
                        }

                        setEffect {
                            ForgotPasswordEffect.Navigation.NavigateToValidateEmailSubScreen
                        }
//                        checkIfOtpBlockedEmail()
                    }
                }
            }
        }
    }

    /**
     * Checks if the OTP functionality for the email is blocked.
     */
    private fun checkIfOtpBlockedEmail() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            forgotPasswordInteractor.isOtpBlockedEmail(currentState.requestIdEmail)
                .collect { forgotPasswordPartialState ->
                    when (forgotPasswordPartialState) {
                        is ForgotPasswordIsOtpBlockedEmailPartialState.NotBlockedEmail -> {
                            // OTP functionality isn't blocked.
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    isResendOtpButtonEnabled = false,
                                    otpFieldEnabled = true
                                )
                            }
                            timerController.startCountdown(forgotPasswordPartialState.otpInterval)
                                .collect { timerStatus ->
                                    when (timerStatus) {
                                        TimerStatus.Completed -> {
                                            setState {
                                                copy(
                                                    otpBlockRemainingTime = null,
                                                    isResendOtpButtonEnabled = true
                                                )
                                            }
                                            setEffect {
                                                ForgotPasswordEffect.OtpFieldRequestFocus
                                            }
                                        }

                                        is TimerStatus.Ticking -> {
                                            setState {
                                                copy(
                                                    otpBlockRemainingTime = timerStatus.remainingTime
                                                )
                                            }
                                        }
                                    }
                                }
                        }

                        is ForgotPasswordIsOtpBlockedEmailPartialState.BlockedEmail -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    otp = "",
                                    isResendOtpButtonEnabled = false,
                                    otpFieldEnabled = false,
                                    isVerifyButtonEnabled = false
                                )
                            }
                            timerController.startCountdown(duration = forgotPasswordPartialState.otpBlocksExpiresIn)
                                .collect { timerStatus ->
                                    when (timerStatus) {
                                        TimerStatus.Completed -> {
                                            setState {
                                                copy(
                                                    otpBlockRemainingTime = null,
                                                    isResendOtpButtonEnabled = true,
                                                    otpFieldEnabled = true
                                                )
                                            }
                                            setEffect {
                                                ForgotPasswordEffect.OtpFieldRequestFocus
                                            }
                                        }

                                        is TimerStatus.Ticking -> {
                                            setState {
                                                copy(
                                                    errorMessage = resourceProvider.getString(
                                                        OtpErrorCause.Blocked.cause,
                                                        timerStatus.remainingTime
                                                    ),
                                                    otpBlockRemainingTime = timerStatus.remainingTime,
                                                )
                                            }
                                        }
                                    }
                                }
                        }

                        is ForgotPasswordIsOtpBlockedEmailPartialState.Failed -> {
                            setEffect {
                                ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError(
                                    errorMessage = forgotPasswordPartialState.errorMessage
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * This method is called when the user-entered OTP changes.
     */
    private fun onOtpFieldValueChange(otp: String) {
        if (otp.isDigitsOnly() && otp.length <= 6) {
            setState {
                copy(
                    errorMessage = null,
                    otp = otp,
                    isVerifyButtonEnabled = otp.length == 6
                )
            }
        }
    }

    private fun validateEmail() {
        viewModelScope.launch {
            formValidator.validateForm(Form(mapOf(getEmailValidationRules() to currentState.email)))
                .collect { formValidationResult ->
                    setState {
                        copy(
                            emailValidationResult = formValidationResult,
                            isContinueButtonEnabled = formValidationResult.isValid
                        )
                    }
                }
        }
    }

    /**
     * Resends an OTP to the provided email address.
     */
    private fun resendOtpToEmail() {
        if (currentState.otpBlockRemainingTime != null) {
            // OTP block is in place. Disable "Resend OTP" button, and don't proceed any further.
            setState {
                copy(
                    isResendOtpButtonEnabled = false
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true
            )
        }
        sendOtpToEmail()
    }

    private fun validateOtpEmail() {
        // Current OTP length is not equal to 6, thus, do NOT proceed with the validation process.
        if (currentState.otp.length != 6) return

        // The length of the user-entered OTP is 6, thus, proceed with the validation process.
        setState {
            copy(
                isLoading = true
            )
        }
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            forgotPasswordInteractor.validateOtp(
                requestId = currentState.requestIdEmail,
                otp = currentState.otp,
                verificationStep = currentState.verificationStep
            ).collect {
                when (it) {
                    is ForgotPasswordValidateOtpPartialState.Success -> {

                    }

                    is ForgotPasswordValidateOtpPartialState.ClientError -> {

                    }

                    is ForgotPasswordValidateOtpPartialState.Failed -> {
                        setEffect {
                            ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError(
                                errorMessage = it.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

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
}