package com.alexgreece45.marvelx.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.controllers.TimerStatus
import com.alexgreece45.marvelx.data.domains.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.domains.resendOtp.ResendOtpErrorCause
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.interactors.GetJwtForSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GetSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GrandNewAccessTokenPartialState
import com.alexgreece45.marvelx.interactors.LoginInteractor
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.RegisterInteractor
import com.alexgreece45.marvelx.interactors.ResendOtpPartialState
import com.alexgreece45.marvelx.interactors.ValidateOtpPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OtpEvent : BaseEvent {
    data class InitView(
        val userId: Int?,
        val verificationStep: String?,
        val shouldRememberUserSession: Boolean?
    ) : OtpEvent()

    data class OnOtpFieldValueChange(val otp: String) : OtpEvent()

    data object OnBackClick : OtpEvent()

    data object OnDoneClick : OtpEvent()

    data object OnVerifyButtonClicked : OtpEvent()

    data object OnResendOtpButtonClick : OtpEvent()
}

data class OtpState(
    // Base states.
    val isLoading: Boolean = true,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,

    /**
     * The user ID.
     * */
    val userId: Int = -1,

    /**
     * The verification step.
     */
    val verificationStep: VerificationStep = VerificationStep.Invalid(""),

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

sealed class OtpEffect : BaseEffect {
    data object OtpFieldRequestFocus : OtpEffect()

    sealed class Navigation : OtpEffect() {
        data object PopBackStack : Navigation()
        data class NavigateToPreviousScreenWithError(val errorMessage: String) : Navigation()

        data object NavigateToAllCharactersScreen : Navigation()
    }

    data object OnUserSensitiveDataReceived : OtpEffect()
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpInteractor: OtpInteractor,
    private val loginInteractor: LoginInteractor,
    private val registerInteractor: RegisterInteractor,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys,
    private val timerController: TimerController
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

    override fun setInitialState(): OtpState = OtpState()

    override fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.InitView -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initView(event.userId, event.verificationStep, event.shouldRememberUserSession)
            }

            is OtpEvent.OnOtpFieldValueChange -> {
                // Update state, ONLY if the new OTP is different from the old one.
                if (event.otp != currentState.otp) {
                    onOtpFieldValueChange(otp = event.otp)
                }
            }

            OtpEvent.OnDoneClick -> {
                if (currentState.otpFieldEnabled) {
                    validateOtp(event)
                }
            }

            OtpEvent.OnBackClick -> {
                setEffect {
                    OtpEffect.Navigation.PopBackStack
                }
            }

            OtpEvent.OnResendOtpButtonClick -> {
                resendOtp()
            }

            OtpEvent.OnVerifyButtonClicked -> {
                if (currentState.otp.length != 6) {
                    setEffect {
                        OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    }
                    return
                }

                validateOtp(event)
            }
        }
    }

    private fun initView(
        userId: Int?,
        verificationStep: String?,
        shouldRememberUserSession: Boolean?
    ) {
        if (userId == null || verificationStep == null || shouldRememberUserSession == null) {
            // User id and/or verification step is null, and/or remember user session is null so go back to the previous screen by displaying that an unknown error occurred.
            setEffect {
                OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
            return
        }

        setState {
            copy(
                userId = userId,
                verificationStep = VerificationStep.of(verificationStep),
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
                        setEffect {
                            OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                errorMessage = it.errorMessage
                            )
                        }
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
                                    setEffect {
                                        OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                            errorMessage = resourceProvider.getGenericErrorMessage()
                                        )
                                    }
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
                                setEffect {
                                    OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                        errorMessage = resourceProvider.getString(
                                            it.responseResendOtpFailedDomain.resendOtpErrorCause.cause
                                        )
                                    )
                                }
                            }

                            is ResendOtpErrorCause.Invalid -> {
                                setEffect {
                                    OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                        errorMessage = resourceProvider.getGenericErrorMessage()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateOtp(event: OtpEvent) {
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
            otpInteractor.validateOtp(otp = currentState.otp, userId = currentState.userId)
                .collect { validateOtpPartialState ->
                    when (validateOtpPartialState) {
                        is ValidateOtpPartialState.Success -> {
                            // User entered OTP was correct, thus, proceed to retrieve the Access & Refresh tokens.
                            grandNewAccessToken(
                                code = validateOtpPartialState.otpDomain.authenticationCode,
                                event = event
                            )
                        }

                        is ValidateOtpPartialState.Failed -> {
                            setEffect {
                                OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                    errorMessage = validateOtpPartialState.errorMessage
                                )
                            }
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
                                    // "blocksExpiresIn" shouldn't be  `null` when the error cause is "Blocked". Thus, propagate "an unknown error occurred" to the user.
                                    setEffect {
                                        OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                            errorMessage = resourceProvider.getGenericErrorMessage()
                                        )
                                    }
                                    return@collect
                                }

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
                                timerController.startCountdown(duration = validateOtpPartialState.otpResponseErrorDomain.blockExpiresIn)
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
                                                            validateOtpPartialState.otpResponseErrorDomain.errorCause.cause,
                                                            timerStatus.remainingTime
                                                        ),
                                                        otpBlockRemainingTime = timerStatus.remainingTime
                                                    )
                                                }
                                            }
                                        }
                                    }
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

    private fun grandNewAccessToken(code: String, event: OtpEvent) {
        viewModelScope.launch {
            loginInteractor.grandNewAccessToken(code = code).collect { accessTokenPartialState ->
                when (accessTokenPartialState) {
                    is GrandNewAccessTokenPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = accessTokenPartialState.errorMessage // TODO: Fix error display. 
                            )
                        }
                    }

                    is GrandNewAccessTokenPartialState.Success -> {
                        // Token was successfully generated (in the server) and retrieved (in the client).

                        // Save userId in device.
                        prefKeys.setUserId(currentState.userId)

                        // User is now logged in. Save the flag.
                        prefKeys.setUserLoggedIn(isLoggedIn = true)

                        // Save remember user session flag.
                        prefKeys.setRememberUserSession(currentState.shouldRememberUserSession)

                        // Navigate the client to AllCharactersScreen
                        setEffect {
                            OtpEffect.Navigation.NavigateToAllCharactersScreen
                        }
                    }
                }
            }
        }
    }

    /** Retrieves the JWT that will be used later for retrieving the user's
     * OTP preferred medium of communication (i.e., email or phone number).
     * This operation is required, as we are accessing sensitive user data.
     *
     * ### Note: The JWT is valid for a short period of time for security reasons (~1 minute).
     */
    private fun retrieveJwt(userId: Int, verificationStep: VerificationStep) {
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
                        setEffect {
                            OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                errorMessage = it.errorMessage
                            )
                        }
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
                        setEffect {
                            OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                errorMessage = it.errorMessage
                            )
                        }
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
                                        }
                                    }
                                }
                            return@collect
                        }

                        // OTP functionality is blocked.

                        if (sensitiveUserDataDomain.otpBlockExpiresIn == null) {
                            setEffect {
                                OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                    errorMessage = resourceProvider.getGenericErrorMessage()
                                )
                            }
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
}