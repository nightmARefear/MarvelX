package com.alexgreece45.marvelx.viewModels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.interactors.GrandNewAccessTokenPartialState
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.interactors.ValidateOtpPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.FormsValidationResult
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OtpEvent : BaseEvent {
    data class InitView(val userId: Int?) : OtpEvent()

    data class OnOtpFieldValueChange(val otp: String) : OtpEvent()

    data object OnDoneClick : OtpEvent()

    data object OnSendClick : OtpEvent()

    data class OnValidationSuccess(val code: String) : OtpEvent()
}

data class OtpState(
    // Base states.
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = true,
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
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpInteractor: OtpInteractor,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {
    override fun setInitialState(): OtpState =
        OtpState(
            isLoading = false,
            hasInternetConnection = true,
            errorMessage = null,
            userId = null,
            otp = "",
            sendButton = false
        )

    override fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.InitView -> {
                initView(event.userId)
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
        }
    }

    private fun initView(userId: Int?) {
        if (userId == null) {
            // User id is null so go back to the previous screen by displaying that an unknown error occurred.
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
                otpInteractor.validateOtp(otpRequest = getOtpRequest())
                    .collect { getOtpPartialState ->
                        when (getOtpPartialState) {
                            is ValidateOtpPartialState.InternalServerError -> {
                                // Internal server error.
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }

                                setEffect {
                                    OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                        errorMessage = getOtpPartialState.errorMessage
                                    )
                                }
                            }

                            ValidateOtpPartialState.NoInternetConnection -> {
                                // No internet connection.
                                setState {
                                    copy(
                                        isLoading = false,
                                        hasInternetConnection = false
                                    )
                                }
                            }

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

                            is ValidateOtpPartialState.ClientError -> {
                                // This error occurred due to the app sending invalid data.
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }
                                setEffect {
                                    OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                                        errorMessage = getOtpPartialState.errorMessage
                                    )
                                }
                            }

                            is ValidateOtpPartialState.UserError -> {
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

    private fun getOtpValidationRules(): List<Rule> =
        listOf(
            Rule.ValidateNotBlank("OTP cannot be empty."),
            Rule.ValidateStringLength(6, "OTP must be 6 characters.")
        )

    private fun getOtpRequest(): OtpRequest =
        OtpRequest(
            userId = currentState.userId.toString(),
            otp = currentState.otp,
            epoch = DateUtil.getSecondsFromEpoch()
        )
}