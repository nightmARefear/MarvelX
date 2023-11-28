package com.alexgreece45.marvelx.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.data.domains.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.domains.resendOtp.ResendOtpErrorCause
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.interactors.GetJwtForSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GetSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GrandNewAccessTokenPartialState
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.RegisterInteractor
import com.alexgreece45.marvelx.interactors.ResendOtpPartialState
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.interactors.ValidateOtpPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OtpEvent : BaseEvent {
    data class InitView(val userId: Int?, val verificationStep: String?) : OtpEvent()

    data class OnOtpFieldValueChange(val otp: String) : OtpEvent()

    data object OnBackClick : OtpEvent()

    data object OnDoneClick : OtpEvent()

    data object OnResendOtpButtonClick : OtpEvent()

    data class OnValidationSuccess(val code: String) : OtpEvent()

    data class OnTokenReceived(val token: String) : OtpEvent()
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

    val resendOtpButtonEnabled: Boolean = true,

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

    data class OnValidationSuccess(val code: String) : OtpEffect()

    data class OnTokenReceived(val token: String, val verificationStep: VerificationStep) :
        OtpEffect()

    data object OnUserSensitiveDataReceived : OtpEffect()
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpInteractor: OtpInteractor,
    private val userInteractor: UserInteractor,
    private val registerInteractor: RegisterInteractor,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys,
    private val timerController: TimerController
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {
    override fun setInitialState(): OtpState = OtpState()

    override fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.InitView -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initView(event.userId, event.verificationStep)
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

            is OtpEvent.OnValidationSuccess -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                grandNewAccessToken(code = event.code, event = event)
            }

            is OtpEvent.OnTokenReceived -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                getSensitiveUserData(token = event.token)
            }

            OtpEvent.OnBackClick -> {
                setEffect {
                    OtpEffect.Navigation.PopBackStack
                }
            }

            OtpEvent.OnResendOtpButtonClick -> {
                resendOtp()
            }
        }
    }

    private fun initView(userId: Int?, verificationStep: String?) {
        if (userId == null || verificationStep == null) {
            // User id and/or verification step is null, so go back to the previous screen by displaying that an unknown error occurred.
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
                verificationStep = VerificationStep.of(verificationStep)
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
                    otp = otp
                )
            }

            // Automatically validate user entered OTP if its length is 6 characters.
            if (otp.length == 6) {
                validateOtp()
            }
        }
    }

    private fun resendOtp() {
        if (currentState.otpBlockRemainingTime != null) {
            // OTP block is in place. Disable "Resend OTP" button, and don't proceed any further.
            setState {
                copy(
                    resendOtpButtonEnabled = false
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
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
                                resendOtpButtonEnabled = false
                            )
                        }
                        timerController.startTimer(expiresIn = it.expiresIn) { formattedTime, isLastSecond ->
                            if (isLastSecond) {
                                setState {
                                    copy(
                                        otpBlockRemainingTime = null,
                                        resendOtpButtonEnabled = true
                                    )
                                }
                            } else {
                                setState {
                                    copy(
                                        otpBlockRemainingTime = formattedTime,
                                    )
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
                                        errorMessage = null,
                                        otp = "",
                                        resendOtpButtonEnabled = false,
                                        otpFieldEnabled = false
                                    )
                                }
                                timerController.startTimer(expiresIn = otpFailedDomain.expiresIn) { formattedTime, isLastSecond ->
                                    if (isLastSecond) {
                                        setState {
                                            copy(
                                                errorMessage = null,
                                                otpBlockRemainingTime = null,
                                                resendOtpButtonEnabled = true,
                                                otpFieldEnabled = true
                                            )
                                        }
                                    } else {
                                        setState {
                                            copy(
                                                errorMessage = resourceProvider.getString(
                                                    otpFailedDomain.resendOtpErrorCause.cause,
                                                    formattedTime
                                                ),
                                                otpBlockRemainingTime = formattedTime,
                                            )
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

    private fun validateOtp() {
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
                            // User entered OTP was correct, thus, proceed to retrieve the Access & Refresh tokens.
                            setEffect {
                                OtpEffect.OnValidationSuccess(validateOtpPartialState.otpDomain.authenticationCode)
                            }
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

                            // If the OTP error cause it Blocked, perform additional operations.
                            if (validateOtpPartialState.otpResponseErrorDomain.errorCause is OtpErrorCause.Blocked) {
                                if (validateOtpPartialState.otpResponseErrorDomain.blockExpiresIn == null) {
                                    // "blocksExpiresIn" is null when the error cause is "Blocked". Propagate "an unknown error occurred" to the user.
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
                                        resendOtpButtonEnabled = false
                                    )
                                }
                                timerController.startTimer(
                                    expiresIn = validateOtpPartialState.otpResponseErrorDomain.blockExpiresIn,
                                    onTick = { formattedRemainingTime, isLastSecond ->
                                        // Current second is the last one.
                                        if (isLastSecond) {
                                            setState {
                                                copy(
                                                    errorMessage = null,
                                                    otpBlockRemainingTime = null,
                                                    otpFieldEnabled = true,
                                                    resendOtpButtonEnabled = true
                                                )
                                            }
                                        } else {
                                            setState {
                                                copy(
                                                    errorMessage = resourceProvider.getString(
                                                        validateOtpPartialState.otpResponseErrorDomain.errorCause.cause,
                                                        formattedRemainingTime
                                                    ),
                                                    otpBlockRemainingTime = formattedRemainingTime
                                                )
                                            }
                                        }
                                    }
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

    private fun grandNewAccessToken(code: String, event: OtpEvent) {
        viewModelScope.launch {
            userInteractor.grandNewAccessToken(code = code).collect { accessTokenPartialState ->
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
                        setEffect {
                            OtpEffect.OnTokenReceived(
                                token = it.jwtSensitiveUserDataDomain.token,
                                verificationStep = verificationStep
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the user's preferred medium of communication for sending OTPs.
     */
    private fun getSensitiveUserData(token: String) {
        viewModelScope.launch {
            userInteractor.getSensitiveUserData(token = token).collect {
                when (it) {
                    is GetSensitiveUserDataPartialState.Failed -> {
                        setEffect {
                            OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                errorMessage = it.errorMessage
                            )
                        }
                    }

                    is GetSensitiveUserDataPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                sensitiveUserData = it.sensitiveUserDataDomain
                            )
                        }
                        // Request focus in OTP field.
                        setEffect {
                            OtpEffect.OnUserSensitiveDataReceived
                        }
                    }
                }
            }
        }
    }

    private fun getOtpValidationRules(): List<Rule> =
        listOf(
            Rule.ValidateNotBlank("OTP cannot be empty."),
            Rule.ValidateStringLength(6, "OTP must be 6 characters.")
        )
}