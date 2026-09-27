package com.alexgreece45.marvelx.ui.screens.otp

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.OAuthAuthorizationController
import com.alexgreece45.marvelx.controllers.OAuthAuthorizationRequest
import com.alexgreece45.marvelx.controllers.OAuthCallbackResult
import com.alexgreece45.marvelx.controllers.OAuthLoginContext
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.controllers.TimerStatus
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordInteractor
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordValidateOtpPartialState
import com.alexgreece45.marvelx.domain.interactors.GetJwtForSensitiveUserDataPartialState
import com.alexgreece45.marvelx.domain.interactors.GetSensitiveUserDataPartialState
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.OtpInteractor
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractor
import com.alexgreece45.marvelx.domain.interactors.ResendOtpPartialState
import com.alexgreece45.marvelx.domain.interactors.ValidateOtpPartialState
import com.alexgreece45.marvelx.domain.model_api.otp.OtpErrorCause
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.domain.model_api.resendOtp.ResendOtpErrorCause
import com.alexgreece45.marvelx.domain.model_api.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.usecases.CompleteOAuthLoginResult
import com.alexgreece45.marvelx.domain.usecases.CompleteOAuthLoginUseCase
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * All the error codes of the `OtpScreen` for error reporting by the users.
 */
private sealed class OtpScreenErrorCodes(val code: String) {
    sealed class InitViewChecks(code: String) : OtpScreenErrorCodes(code) {
        data object UnexpectedError : InitViewChecks("OTP_1000")
        data object NullableFields : InitViewChecks("OTP_1001")
        data object InvalidOtpFlowType : InitViewChecks("OTP_1002")
        data object InvalidVerificationStep : InitViewChecks("OTP_1003")
    }

    sealed class RetrieveJwt(code: String) : OtpScreenErrorCodes(code) {
        data object GenericError : RetrieveJwt("OTP_1004")
    }

    sealed class GetSensitiveUserData(code: String) : OtpScreenErrorCodes(code) {
        data object GenericError : GetSensitiveUserData("OTP_1005")
        data object OtpBlocksExpiresInNull : GetSensitiveUserData("OTP_1006")
    }

    sealed class GrandNewAccessToken(code: String) : OtpScreenErrorCodes(code) {
        data object GenericError : GrandNewAccessToken("OTP_1007")
    }

    sealed class OAuthAuthorization(code: String) : OtpScreenErrorCodes(code) {
        data object Cancelled : OAuthAuthorization("OTP_1012")
    }

    sealed class PostLogin(code: String) : OtpScreenErrorCodes(code) {
        data object GenericError : PostLogin("OTP_1008")
    }

    sealed class ForgotPasswordValidateOtp(code: String) : OtpScreenErrorCodes(code) {
        data object GenericError : ForgotPasswordValidateOtp("OTP_1009")
        data object Email : ForgotPasswordValidateOtp("OTP_1010")
        data object PhoneNumber : ForgotPasswordValidateOtp("OTP_1011")
    }
}

internal data class OtpScreenErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val onCloseClicked: (() -> Unit)? = null,
    val errorButtonLayout: ErrorButtonLayout.Single
)

internal sealed class OtpEvent : BaseEvent {
    data class Init(
        val userId: String?,
        val otpFlowType: String?,
        val verificationStep: String?,
        val shouldRememberUserSession: Boolean?,
        val requestIdEmail: String?,
        val requestIdPhoneNumber: String?
    ) : OtpEvent()

    data class OnOtpFieldValueChange(val otp: String) : OtpEvent()

    data object OnBackClick : OtpEvent()

    data object OnDoneClick : OtpEvent()

    data object OnVerifyButtonClicked : OtpEvent()

    data object OnResendOtpButtonClick : OtpEvent()
}

internal data class OtpState(
    // Base states.
    val isLoading: Boolean = true,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,
    val error: OtpScreenErrorView? = null,

    /**
     * The user ID.
     * */
    val userId: String = "",

    val requestIdEmail: String = "",
    val requestIdPhoneNumber: String = "",

    /**
     * The verification step.
     */
    val verificationStep: VerificationStep = VerificationStep.Invalid(""),

    /**
     * The OTP flow type. Either `Forgot Password` or `Register`.
     */
    val otpFlowType: OtpFlowType,

    /**
     * Defines if the application should remember the session when the user exits the app.
     */
    val shouldRememberUserSession: Boolean = false,

    /**
     * The OTP that the user has entered in the TextField.
     * */
    val otp: String = "",

    val sensitiveUserData: SensitiveUserDataDomain? = null,

    /**
     * The amount of remaining time, as a [String], until the OTP functionality is enabled so that
     * new OTPs can be requested. If set to `null`, the OTP functionality is enabled.
     */
    val otpBlockRemainingTime: String? = null,

    val otpFieldEnabled: Boolean = true,

    val isResendOtpButtonEnabled: Boolean = true,

    /**
     * Controls whether the verify button is enabled. When clicked, the user-entered OTP is verified.
     * This button must _only_ be enabled when the length of the OTP is 6. Also, when the OTP field
     * clears, this button should be disabled.
     */
    val isVerifyButtonEnabled: Boolean = false,

    // Validations.
    val enteredOtpValidationResult: FormsValidationResult = FormsValidationResult(true)

) : BaseState

internal sealed class OtpEffect : BaseEffect {
    data object OtpFieldRequestFocus : OtpEffect()
    data object CloseKeyboardAndRemoveFocus : OtpEffect()

    sealed class Navigation : OtpEffect() {
        data object GoBack : Navigation()

        data class NavigateToChangePasswordScreen(
            val requestIdEmail: String,
            val requestIdPhoneNumber: String?
        ) : Navigation()

        data object NavigateToDashboardScreen : Navigation()
    }

    data object OnUserSensitiveDataReceived : OtpEffect()

    data object SaveAutoFilledData : OtpEffect()

    /**
     * Opens [request] (the OAuth 2.0 + PKCE authorization request) in the browser. The result of
     * the flow arrives asynchronously through `MainActivity`, not through this effect.
     */
    data class LaunchOAuthAuthorization(val request: OAuthAuthorizationRequest) : OtpEffect()
}

@KoinViewModel
internal class OtpViewModel(
    private val otpInteractor: OtpInteractor,
    private val loginInteractor: LoginInteractor,
    private val registerInteractor: RegisterInteractor,
    private val forgotPasswordInteractor: ForgotPasswordInteractor,
    private val resourceProvider: ResourceProvider,
    private val timerController: TimerController,
    private val oAuthAuthorizationController: OAuthAuthorizationController,
    private val completeOAuthLoginUseCase: CompleteOAuthLoginUseCase,
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {

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

    /**
     * Tracks the coroutine suspended in [OAuthAuthorizationController.awaitCallback] so it gets
     * cancelled (rather than leaking until its own timeout) if the screen is torn down first.
     */
    private var oAuthCallbackJob: Job? = null

    override fun setInitialState(): OtpState = OtpState(
        otpFlowType = OtpFlowType.Invalid("")
    )

    override fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.Init -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }

                // Invalid nullable field checks.
                if (
                    event.otpFlowType.isNullOrBlank()
                    || event.verificationStep.isNullOrBlank()
                ) {
                    showGenericErrorViewWithErrorCode(
                        errorCode = OtpScreenErrorCodes.InitViewChecks.NullableFields,
                        otpFlowType = OtpFlowType.Invalid("")
                    )
                    return
                }

                // Invalid otp flow type checks.
                val finalOtpFlowType = OtpFlowType.getType(event.otpFlowType)
                if (
                    finalOtpFlowType is OtpFlowType.Invalid
                    || (finalOtpFlowType is OtpFlowType.Register && event.userId == null)
                    || (finalOtpFlowType is OtpFlowType.Register && event.shouldRememberUserSession == null)
                    || (finalOtpFlowType is OtpFlowType.ForgotPassword && event.requestIdEmail.isNullOrBlank())
                ) {
                    showGenericErrorViewWithErrorCode(
                        errorCode = OtpScreenErrorCodes.InitViewChecks.InvalidOtpFlowType,
                        otpFlowType = finalOtpFlowType
                    )
                    return
                }

                // Invalid verification step checks.
                val finalVerificationStep = VerificationStep.of(event.verificationStep)
                if (
                    finalVerificationStep is VerificationStep.Invalid
                    || (finalVerificationStep is VerificationStep.OtpPhoneNumber && event.requestIdPhoneNumber.isNullOrBlank())
                ) {
                    showGenericErrorViewWithErrorCode(
                        errorCode = OtpScreenErrorCodes.InitViewChecks.InvalidVerificationStep,
                        otpFlowType = finalOtpFlowType
                    )
                    return
                }

                when (finalOtpFlowType) {
                    OtpFlowType.Register -> {
                        initRegisterFlow(
                            userId = event.userId,
                            verificationStep = finalVerificationStep,
                            otpFlowType = finalOtpFlowType,
                            shouldRememberUserSession = event.shouldRememberUserSession
                        )
                    }

                    OtpFlowType.ForgotPassword.Email, OtpFlowType.ForgotPassword.PhoneNumber -> {
                        initForgotPasswordFlow(
                            verificationStep = finalVerificationStep,
                            otpFlowType = finalOtpFlowType,
                            requestIdEmail = event.requestIdEmail.orEmpty(),
                            requestIdPhoneNumber = event.requestIdPhoneNumber.orEmpty()
                        )
                    }

                    else -> {
                        showGenericErrorViewWithErrorCode(
                            errorCode = OtpScreenErrorCodes.InitViewChecks.UnexpectedError,
                            otpFlowType = finalOtpFlowType
                        )

                        // Just in case any further code is added, in the future, after the `when` block.
                        return
                    }
                }
            }

            is OtpEvent.OnOtpFieldValueChange -> {
                // Update state, ONLY if the new OTP is different from the old one.
                if (event.otp != currentState.otp) {
                    onOtpFieldValueChange(otp = event.otp)
                }
            }

            OtpEvent.OnDoneClick -> {
                if (currentState.otpFieldEnabled) {
                    validateOtp()
                }
            }

            OtpEvent.OnBackClick -> {
                if (currentState.isLoading) return

                setEffect {
                    OtpEffect.Navigation.GoBack
                }
            }

            OtpEvent.OnResendOtpButtonClick -> {
                resendOtp()
            }

            OtpEvent.OnVerifyButtonClicked -> {
                if (currentState.otp.length != 6) {
                    showGenericErrorViewWithErrorMessage(
                        errorMessage = resourceProvider.getGenericErrorMessage(),
                        otpFlowType = currentState.otpFlowType
                    )
                    return
                }

                validateOtp()
            }
        }
    }

    /**
     * The initial operations to be executed when the `Register` flow (i.e., login, or signup)
     * begins.
     */
    private fun initRegisterFlow(
        userId: String?,
        verificationStep: VerificationStep,
        otpFlowType: OtpFlowType,
        shouldRememberUserSession: Boolean?
    ) {
        if (userId == null || shouldRememberUserSession == null) {
            showGenericErrorViewWithErrorCode(
                errorCode = OtpScreenErrorCodes.InitViewChecks.NullableFields,
                otpFlowType = OtpFlowType.Register
            )
            return
        }

        setState {
            copy(
                userId = userId,
                verificationStep = verificationStep,
                otpFlowType = otpFlowType,
                shouldRememberUserSession = shouldRememberUserSession
            )
        }

        // Get JWT for retrieving sensitive user data (i.e., the preferred
        // medium of communication for the OTP).
        retrieveJwt(
            userId = currentState.userId,
            verificationStep = currentState.verificationStep
        )
    }

    /**
     * The initial operations to be executed when the `Forgot Password` flow begins.
     */
    private fun initForgotPasswordFlow(
        verificationStep: VerificationStep,
        otpFlowType: OtpFlowType,
        requestIdEmail: String,
        requestIdPhoneNumber: String
    ) {
        setState {
            copy(
                requestIdEmail = requestIdEmail,
                requestIdPhoneNumber = requestIdPhoneNumber,
                verificationStep = verificationStep,
                otpFlowType = otpFlowType
            )
        }

        when (verificationStep) {
            VerificationStep.OtpEmail -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
            }

            // Phone number OTP is not supported yet, so it falls through to the error view.
            else -> {
                showGenericErrorViewWithErrorCode(
                    errorCode = OtpScreenErrorCodes.InitViewChecks.InvalidVerificationStep,
                    otpFlowType = currentState.otpFlowType
                )
            }
        }
    }

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

    private fun resendOtp() {
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
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            otpInteractor.resendOtp(
                userId = currentState.userId,
                verificationStep = currentState.verificationStep
            ).collect {
                when (it) {
                    is ResendOtpPartialState.Error -> {
                        showGenericErrorViewWithErrorMessage(
                            errorMessage = it.errorMessage,
                            otpFlowType = OtpFlowType.Register
                        )
                    }

                    is ResendOtpPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = null,
                                isResendOtpButtonEnabled = false
                            )
                        }
                        timerController.startCountdown(duration = it.expiresIn)
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
                                            OtpEffect.OtpFieldRequestFocus
                                        }
                                    }

                                    is TimerStatus.Ticking -> {
                                        setState {
                                            copy(
                                                otpBlockRemainingTime = timerStatus.remainingTime,
                                            )
                                        }
                                    }
                                }
                            }
                    }

                    is ResendOtpPartialState.Failed -> {
                        val otpFailedDomain = it.responseResendOtpFailedDomain

                        when (otpFailedDomain.resendOtpErrorCause) {
                            ResendOtpErrorCause.Blocked, ResendOtpErrorCause.TooSoon -> {
                                if (otpFailedDomain.expiresIn == null) {
                                    showGenericErrorViewWithErrorMessage(
                                        errorMessage = resourceProvider.getGenericErrorMessage(),
                                        otpFlowType = OtpFlowType.Register
                                    )
                                    return@collect
                                }

                                setState {
                                    copy(
                                        isLoading = false,
                                        otp = "",
                                        isResendOtpButtonEnabled = false,
                                        otpFieldEnabled = false,
                                        isVerifyButtonEnabled = false
                                    )
                                }
                                timerController.startCountdown(duration = otpFailedDomain.expiresIn)
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
                                                    OtpEffect.OtpFieldRequestFocus
                                                }
                                            }

                                            is TimerStatus.Ticking -> {
                                                setState {
                                                    copy(
                                                        errorMessage = resourceProvider.getString(
                                                            otpFailedDomain.resendOtpErrorCause.cause,
                                                            timerStatus.remainingTime
                                                        ),
                                                        otpBlockRemainingTime = timerStatus.remainingTime,
                                                    )
                                                }
                                            }
                                        }
                                    }
                            }

                            ResendOtpErrorCause.InternalServerError, ResendOtpErrorCause.UserNotFound -> {
                                showGenericErrorViewWithErrorMessage(
                                    errorMessage = resourceProvider.getString(
                                        it.responseResendOtpFailedDomain.resendOtpErrorCause.cause
                                    ),
                                    otpFlowType = OtpFlowType.Register
                                )
                            }

                            is ResendOtpErrorCause.Invalid -> {
                                showGenericErrorViewWithErrorMessage(
                                    errorMessage = resourceProvider.getGenericErrorMessage(),
                                    otpFlowType = OtpFlowType.Register
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the user-entered OTP based on the [OtpFlowType].
     */
    private fun validateOtp() {
        setEffect {
            OtpEffect.CloseKeyboardAndRemoveFocus
        }
        when (currentState.otpFlowType) {
            is OtpFlowType.Invalid -> {
                showGenericErrorViewWithErrorCode(
                    errorCode = OtpScreenErrorCodes.InitViewChecks.InvalidOtpFlowType,
                    otpFlowType = currentState.otpFlowType
                )
            }

            OtpFlowType.ForgotPassword.Email, OtpFlowType.ForgotPassword.PhoneNumber -> {
                forgotPasswordValidateOtp()
            }

            OtpFlowType.Register -> {
                validateOtpRegisterFlow()
            }
        }
    }

    private fun validateOtpRegisterFlow() {
        // Current OTP length is not equal to 6, thus, do NOT proceed with the validation process.
        if (currentState.otp.length != 6) return

        // The length of the user-entered OTP is 6, thus, proceed with the validation process.
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            otpInteractor.validateOtp(otp = currentState.otp, userId = currentState.userId)
                .collect { validateOtpPartialState ->
                    when (validateOtpPartialState) {
                        is ValidateOtpPartialState.Success -> {
                            // Retrieve authorization code.
                            getAuthorizationCode(validateOtpPartialState.otpDomain.tempAuthCode)
                        }

                        is ValidateOtpPartialState.Failed -> {
                            showGenericErrorViewWithErrorMessage(
                                errorMessage = validateOtpPartialState.errorMessage,
                                otpFlowType = OtpFlowType.Register
                            )
                        }

                        is ValidateOtpPartialState.ClientError -> {
                            // User-input related error.
                            setState {
                                copy(
                                    isVerifyButtonEnabled = false
                                )
                            }

                            // If the OTP error cause it Blocked, perform additional operations.
                            if (validateOtpPartialState.otpResponseErrorDomain.errorCause is OtpErrorCause.Blocked) {
                                if (validateOtpPartialState.otpResponseErrorDomain.blockExpiresIn == null) {
                                    // "blocksExpiresIn" shouldn't be `null` when the error cause is "Blocked". Thus, propagate "an unknown error occurred" to the user.
                                    showGenericErrorViewWithErrorMessage(
                                        errorMessage = resourceProvider.getGenericErrorMessage(),
                                        otpFlowType = OtpFlowType.Register
                                    )
                                    return@collect
                                }

                                setBlockedTimer(
                                    blockExpiresIn = validateOtpPartialState.otpResponseErrorDomain.blockExpiresIn,
                                    otpErrorCause = validateOtpPartialState.otpResponseErrorDomain.errorCause
                                )
                                return@collect
                            }

                            // OTP error cause is NOT "Blocked". (TODO: review this case)
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = resourceProvider.getString(
                                        validateOtpPartialState.otpResponseErrorDomain.errorCause.cause
                                    ),
                                    otp = "",
                                    otpFieldEnabled = true
                                )
                            }

                            // Request focus for OTP field.
                            setEffect {
                                OtpEffect.OtpFieldRequestFocus
                            }
                        }
                    }
                }
        }
    }

    /**
     * Opens the OAuth 2.0 + PKCE authorization request in the browser and suspends until the
     * browser redirects back into the app with an authorization code.
     */
    private fun getAuthorizationCode(tempAuthCode: String) {
        oAuthCallbackJob?.cancel()
        oAuthCallbackJob = viewModelScope.launch {
            val request = oAuthAuthorizationController.startAuthorization(
                tempAuthCode = tempAuthCode,
                loginContext = OAuthLoginContext(
                    userId = currentState.userId,
                    shouldRememberUserSession = currentState.shouldRememberUserSession
                )
            )
            setEffect {
                OtpEffect.LaunchOAuthAuthorization(request)
            }

            when (val result = oAuthAuthorizationController.awaitCallback()) {
                is OAuthCallbackResult.Success -> {
                    completeLogin(result)
                }

                is OAuthCallbackResult.ServerError,
                OAuthCallbackResult.StateMismatch,
                OAuthCallbackResult.Timeout -> {
                    showGenericErrorViewWithErrorCode(
                        errorCode = OtpScreenErrorCodes.GrandNewAccessToken.GenericError,
                        otpFlowType = OtpFlowType.Register
                    )
                }

                // The user returned from the browser without completing the authorization.
                OAuthCallbackResult.Cancelled -> {
                    showGenericErrorViewWithErrorCode(
                        errorCode = OtpScreenErrorCodes.OAuthAuthorization.Cancelled,
                        otpFlowType = OtpFlowType.Register
                    )
                }
            }
        }
    }

    /**
     * Exchanges the authorization code for tokens and completes the login.
     */
    private suspend fun completeLogin(authorization: OAuthCallbackResult.Success) {
        val result = completeOAuthLoginUseCase(
            code = authorization.code,
            codeVerifier = authorization.codeVerifier,
            redirectUri = authorization.redirectUri,
            userId = currentState.userId,
            shouldRememberUserSession = currentState.shouldRememberUserSession
        )

        when (result) {
            CompleteOAuthLoginResult.Success -> {
                // Show autofill data save prompt (it saves the data from the login screen).
                setEffect {
                    OtpEffect.SaveAutoFilledData
                }

                setEffect {
                    OtpEffect.Navigation.NavigateToDashboardScreen
                }
            }

            is CompleteOAuthLoginResult.TokenExchangeFailed -> {
                showGenericErrorViewWithErrorCode(
                    errorCode = OtpScreenErrorCodes.GrandNewAccessToken.GenericError,
                    otpFlowType = OtpFlowType.Register
                )
            }

            is CompleteOAuthLoginResult.PostLoginFailed -> {
                showGenericErrorViewWithErrorCode(
                    errorCode = OtpScreenErrorCodes.PostLogin.GenericError,
                    otpFlowType = OtpFlowType.Register
                )
            }
        }
    }

    /** Retrieves the JWT that will be used later for retrieving the user's
     * OTP preferred medium of communication (i.e., email or phone number).
     * This operation is required, as we are accessing sensitive user data.
     *
     * ### Note: The JWT is valid for a short period of time for security reasons (~1 minute).
     */
    private fun retrieveJwt(userId: String, verificationStep: VerificationStep) {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            registerInteractor.getJwtForSensitiveUserData(
                userId = userId,
                verificationStep = verificationStep
            ).collect {
                when (it) {
                    is GetJwtForSensitiveUserDataPartialState.Failed -> {
                        showGenericErrorViewWithErrorCode(
                            errorCode = OtpScreenErrorCodes.RetrieveJwt.GenericError,
                            otpFlowType = OtpFlowType.Register
                        )
                    }

                    is GetJwtForSensitiveUserDataPartialState.Success -> {
                        getSensitiveUserData(it.jwtSensitiveUserDataDomain.token)
                    }
                }
            }
        }
    }

    /**
     * Retrieves the user's preferred medium of communication for sending OTPs.
     */
    private fun getSensitiveUserData(token: String) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            loginInteractor.getSensitiveUserData(token = token).collect {
                when (it) {
                    is GetSensitiveUserDataPartialState.Failed -> {
                        showGenericErrorViewWithErrorCode(
                            errorCode = OtpScreenErrorCodes.GetSensitiveUserData.GenericError,
                            otpFlowType = OtpFlowType.Register
                        )
                    }

                    is GetSensitiveUserDataPartialState.Success -> {
                        val sensitiveUserDataDomain = it.sensitiveUserDataDomain
                        if (sensitiveUserDataDomain.isOtpBlocked.not()) {
                            // OTP functionality isn't blocked.

                            setState {
                                copy(
                                    isLoading = false,
                                    sensitiveUserData = sensitiveUserDataDomain
                                )
                            }

                            // Block "Resend OTP functionality."
                            timerController.startCountdown(sensitiveUserDataDomain.otpInterval)
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
                                                OtpEffect.OtpFieldRequestFocus
                                            }
                                        }

                                        is TimerStatus.Ticking -> {
                                            setState {
                                                copy(
                                                    otpBlockRemainingTime = timerStatus.remainingTime,
                                                    isResendOtpButtonEnabled = false
                                                )
                                            }
                                            setEffect {
                                                OtpEffect.OtpFieldRequestFocus
                                            }
                                        }
                                    }
                                }
                            return@collect
                        }

                        // OTP functionality is blocked.

                        if (sensitiveUserDataDomain.otpBlockExpiresIn == null) {
                            showGenericErrorViewWithErrorCode(
                                errorCode = OtpScreenErrorCodes.GetSensitiveUserData.OtpBlocksExpiresInNull,
                                otpFlowType = OtpFlowType.Register
                            )
                            return@collect
                        }

                        setState {
                            copy(
                                isLoading = false,
                                otpFieldEnabled = !sensitiveUserDataDomain.isOtpBlocked,
                                isResendOtpButtonEnabled = false,
                                sensitiveUserData = it.sensitiveUserDataDomain
                            )
                        }
                        timerController.startCountdown(duration = sensitiveUserDataDomain.otpBlockExpiresIn)
                            .collect { timerStatus ->
                                when (timerStatus) {
                                    TimerStatus.Completed -> {
                                        setState {
                                            copy(
                                                errorMessage = null,
                                                otpBlockRemainingTime = null,
                                                otpFieldEnabled = true,
                                                isResendOtpButtonEnabled = true
                                            )
                                        }
                                        setEffect {
                                            OtpEffect.OtpFieldRequestFocus
                                        }
                                    }

                                    is TimerStatus.Ticking -> {
                                        setState {
                                            copy(
                                                errorMessage = resourceProvider.getString(
                                                    OtpErrorCause.Blocked.cause,
                                                    timerStatus.remainingTime
                                                ),
                                                otpBlockRemainingTime = timerStatus.remainingTime
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    // *********************************************************************************************
    // *                       `Forgot Password flow` functions                                    *
    // *********************************************************************************************

    /**
     * This function is triggered when the user clicks on the `Validate OTP` button of the `forgot
     * password flow`. It validates the user-entered OTP against the one that was sent to their
     * email / phone number.
     */
    private fun forgotPasswordValidateOtp() {
        if (currentState.otp.length != 6) {
            showGenericErrorViewWithErrorCode(
                errorCode = if (currentState.otpFlowType is OtpFlowType.ForgotPassword.Email) OtpScreenErrorCodes.ForgotPasswordValidateOtp.Email else OtpScreenErrorCodes.ForgotPasswordValidateOtp.PhoneNumber,
                otpFlowType = currentState.otpFlowType
            )
            return
        }

        setState {
            copy(
                isLoading = true
            )
        }
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            forgotPasswordInteractor.validateOtp(
                requestId = if (currentState.otpFlowType is OtpFlowType.ForgotPassword.Email) currentState.requestIdEmail else currentState.requestIdPhoneNumber,
                otp = currentState.otp,
                verificationStep = currentState.verificationStep
            ).collect {
                when (it) {
                    is ForgotPasswordValidateOtpPartialState.Success -> {
                        when (it.isPhoneNumberRequired) {
                            true -> {
                                // NOT IMPLEMENTED YET!

                                // Step 1: Prompt user to enter their phone number from the previous screen.

                                // Step 2: Send OTP to phone number and retrieve requestIdPhoneNumber.
//                                forgotPasswordInteractor.sendOtp()
//                                    .collect { sendOtpPartialState ->
//                                        when (sendOtpPartialState) {
//                                            is SendOtpToPhoneNumberPartialState.Failed -> {}
//                                            is SendOtpToPhoneNumberPartialState.Success -> {
//                                                // Step 3: Init forgot password flow with requestIdEmail + requestIdPhoneNumber
//                                                initViewForgotPasswordFlow(
//                                                    verificationStep = VerificationStep.OtpPhoneNumber,
//                                                    otpFlowType = currentState.otpFlowType,
//                                                    requestIdEmail = currentState.requestIdEmail,
//                                                    requestIdPhoneNumber = sendOtpPartialState.requestId
//                                                )
//                                            }
//                                        }
//                                    }
                                // TODO: Remove the code below when the above functionality has been added.
                                setEffect {
                                    OtpEffect.Navigation.NavigateToChangePasswordScreen(
                                        requestIdEmail = currentState.requestIdEmail,
                                        requestIdPhoneNumber = currentState.requestIdPhoneNumber
                                    )
                                }
                            }

                            false -> {
                                setEffect {
                                    OtpEffect.Navigation.NavigateToChangePasswordScreen(
                                        requestIdEmail = currentState.requestIdEmail,
                                        requestIdPhoneNumber = currentState.requestIdPhoneNumber // todo: review case
                                    )
                                }
                            }
                        }
                    }

                    is ForgotPasswordValidateOtpPartialState.ClientError -> {
                        setState {
                            copy(
                                isVerifyButtonEnabled = false
                            )
                        }

                        if (it.otpResponseErrorDomain.errorCause is OtpErrorCause.Blocked) {
                            // set blocked timer.
                            setBlockedTimer(
                                blockExpiresIn = it.otpResponseErrorDomain.blockExpiresIn ?: 0L,
                                otpErrorCause = it.otpResponseErrorDomain.errorCause
                            )
                            return@collect
                        }

                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = resourceProvider.getString(
                                    it.otpResponseErrorDomain.errorCause.cause
                                ),
                                otp = "",
                                otpFieldEnabled = true
                            )
                        }

                        setEffect {
                            OtpEffect.OtpFieldRequestFocus
                        }
                    }

                    is ForgotPasswordValidateOtpPartialState.Failed -> {
                        showGenericErrorViewWithErrorMessage(
                            errorMessage = it.errorMessage,
                            otpFlowType = currentState.otpFlowType
                        )
                    }
                }
            }
        }
    }

    // *********************************************************************************************
    // *                        operational functions                                              *
    // *********************************************************************************************

    /**
     * Sets a blocked OTP timer based on [blockExpiresIn], and displays the appropriate error
     * message by the given [OtpErrorCause].
     */
    private fun setBlockedTimer(blockExpiresIn: Long, otpErrorCause: OtpErrorCause) {
        setState {
            copy(
                isLoading = false,
                errorMessage = null,
                otp = "",
                otpFieldEnabled = false,
                isResendOtpButtonEnabled = false,
                otpBlockRemainingTime = null,
            )
        }

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            timerController.startCountdown(duration = blockExpiresIn)
                .collect { timerStatus ->
                    when (timerStatus) {
                        TimerStatus.Completed -> {
                            setState {
                                copy(
                                    errorMessage = null,
                                    otpBlockRemainingTime = null,
                                    otpFieldEnabled = true,
                                    isResendOtpButtonEnabled = true
                                )
                            }
                            setEffect {
                                OtpEffect.OtpFieldRequestFocus
                            }
                        }

                        is TimerStatus.Ticking -> {
                            timerStatus.remainingTime.let { remainingTime ->
                                setState {
                                    copy(
                                        errorMessage = resourceProvider.getString(
                                            otpErrorCause.cause,
                                            remainingTime
                                        ),
                                        otpBlockRemainingTime = remainingTime
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    // *********************************************************************************************
    // *                        `Error` functions                                                  *
    // *********************************************************************************************

    /**
     * Displays the error view with the given [errorCode] and the `on-close` & `primary` buttons
     * navigate the user to the previous screen.
     */
    private fun showGenericErrorViewWithErrorCode(
        errorCode: OtpScreenErrorCodes,
        otpFlowType: OtpFlowType
    ) {
        showGenericErrorViewWithErrorMessage(
            errorMessage = resourceProvider.getString(
                R.string.otp_screen_generic_error_subtitle_with_error_code,
                errorCode.code
            ),
            otpFlowType = otpFlowType
        )
    }

    /**
     * Displays the error view with the given [errorMessage] and the `on-close` & `primary` buttons
     * navigate the user to the previous screen.
     */
    private fun showGenericErrorViewWithErrorMessage(
        errorMessage: String,
        otpFlowType: OtpFlowType
    ) {
        setState {
            copy(
                error = OtpScreenErrorView(
                    errorTitle = resourceProvider.getString(R.string.otp_screen_generic_error_title),
                    errorSubtitle = errorMessage,
                    onCloseClicked = {
                        setEffect {
                            OtpEffect.Navigation.GoBack
                        }
                    },
                    errorButtonLayout = ErrorButtonLayout.Single(
                        primaryButtonLabel = when (otpFlowType) {
                            is OtpFlowType.Invalid -> resourceProvider.getString(R.string.generic_go_to_previous_screen_button)
                            OtpFlowType.Register -> resourceProvider.getString(R.string.otp_screen_back_to_login_button)
                            OtpFlowType.ForgotPassword.Email -> resourceProvider.getString(R.string.generic_go_to_previous_screen_button)
                            OtpFlowType.ForgotPassword.PhoneNumber -> resourceProvider.getString(R.string.generic_go_to_previous_screen_button)
                        },
                        onPrimaryButtonClicked = {
                            setEffect {
                                OtpEffect.Navigation.GoBack
                            }
                        }
                    )
                )
            )
        }
    }
}
