package com.alexgreece45.marvelx.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.interactors.GetJwtForSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GetSensitiveUserDataPartialState
import com.alexgreece45.marvelx.interactors.GrandNewAccessTokenPartialState
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.RegisterInteractor
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

    data object OnDoneClick : OtpEvent()

    data object OnSendClick : OtpEvent()

    data class OnValidationSuccess(val code: String) : OtpEvent()

    data class OnJwtRetrieval(val jwt: String, val verificationStep: VerificationStep) : OtpEvent()
}

data class OtpState(
    // Base states.
    val isLoading: Boolean = false,

    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,

    /**
     * The user ID.
     * */
    val userId: Int? = null,

    /**
     * The OTP that the user has entered in the TextField.
     * */
    val otp: String = "",

    val sensitiveUserData: SensitiveUserDataDomain? = null,

    /**
     * Controls whether the send toolbar action button is enabled.
     * This button is enabled if the OTP's length is equal to 6.
     * */
    val sendButton: Boolean = false,

    // Validations.
    val enteredOtpValidationResult: FormsValidationResult = FormsValidationResult(true)

) : BaseState

sealed class OtpEffect : BaseEffect {

    sealed class Navigation : OtpEffect() {
        data class NavigateToPreviousScreenWithError(val errorMessage: String) : Navigation()

        data object NavigateToAllCharactersScreen : Navigation()
    }

    data class OnValidationSuccess(val code: String) : OtpEffect()

    data class OnJwtRetrieval(val jwt: String, val verificationStep: VerificationStep) : OtpEffect()
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpInteractor: OtpInteractor,
    private val userInteractor: UserInteractor,
    private val registerInteractor: RegisterInteractor,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {
    override fun setInitialState(): OtpState =
        OtpState(
            isLoading = false,
            errorMessage = null,
            userId = null,
            otp = "",
            sensitiveUserData = null,
            sendButton = false
        )

    override fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.InitView -> {
                initView(event.userId, event.verificationStep)
            }

            is OtpEvent.OnOtpFieldValueChange -> {
                onOtpFieldValueChange(otp = event.otp)
            }

            OtpEvent.OnDoneClick -> {
                validateOtp()
            }

            OtpEvent.OnSendClick -> {
                validateOtp()
            }

            is OtpEvent.OnValidationSuccess -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                grandNewAccessToken(code = event.code, event = event)
            }

            is OtpEvent.OnJwtRetrieval -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                getSensitiveUserData(jwt = event.jwt, verificationStep = event.verificationStep)
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
                userId = userId
            )
        }

        // Get JWT for retrieving sensitive user data (i.e., the preferred
        // medium of contact for the OTP).
        retrieveJwt(
            userId = userId,
            verificationStep = VerificationStep.of(verificationStep)
        )
    }

    private fun onOtpFieldValueChange(otp: String) {
        if (otp.isDigitsOnly() && otp.length <= 6 && otp != currentState.otp) {
            setState {
                copy(
                    otp = otp,
                    sendButton = otp.length == 6
                )
            }
        }
    }

    private fun validateOtp() {
        // If the length of the user-entered OTP is 6, then proceed with its validation.
        if (currentState.otp.length == 6) {
            setState {
                copy(
                    isLoading = true
                )
            }
            viewModelScope.launch {
                otpInteractor.validateOtp(otp = currentState.otp)
                    .collect { getOtpPartialState ->
                        when (getOtpPartialState) {
                            is ValidateOtpPartialState.Success -> {
                                // OTPs matched.
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }

                                // If the user-entered OTP was correct, then move on to retrieving the Access & Refresh tokens.
                                setEffect {
                                    OtpEffect.OnValidationSuccess(getOtpPartialState.otpDomain.authenticationCode)
                                }
                            }

                            is ValidateOtpPartialState.Failed -> {
                                setEffect {
                                    OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                        errorMessage = getOtpPartialState.errorMessage
                                    )
                                }
                            }

                            is ValidateOtpPartialState.ClientError -> {
                                // User-input related error.
                                setState {
                                    copy(
                                        isLoading = false,
                                        errorMessage = getOtpPartialState.otpErrorCause.cause
                                    )
                                }
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
                        setState {
                            copy(
                                isLoading = false
                            )
                        }

                        // Save userId in device.
                        currentState.userId?.let { prefKeys.setUserId(it) }

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
                            OtpEffect.OnJwtRetrieval(
                                jwt = it.jwtSensitiveUserDataDomain.jwt,
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
    private fun getSensitiveUserData(jwt: String, verificationStep: VerificationStep) {
        viewModelScope.launch {
            userInteractor.getSensitiveUserData(jwt = jwt, verificationStep).collect {
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