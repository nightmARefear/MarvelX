package com.alexgreece45.marvelx.domain.viewmodels

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.domain.interactors.GetOtpPartialState
import com.alexgreece45.marvelx.domain.interactors.OtpInteractor
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
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

    object OnDoneClick : OtpEvent()

    object OnSendClick : OtpEvent()
}

data class OtpState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    /**
     * Error message retrieved from the MarvelX-Backend API for invalid user input.
     * */
    val errorMessage: String? = null,

    // Data.
    val userId: Int? = null,

    /**
     * The OTP that the user has entered in the TextField.
     * */
    val otp: String = "",

    // Validations.
    val enteredOtpValidationResult: FormsValidationResult = FormsValidationResult(true)

) : BaseState {
    companion object {
        fun getDefaultValues(): OtpState =
            OtpState(
                isLoading = true,
                hasInternetConnection = true,
                errorMessage = null,
                userId = null
            )
    }
}

sealed class OtpEffect : BaseEffect {

    sealed class Navigation : OtpEffect() {
        data class NavigateToPreviousScreenWithError(val errorMessage: String) : Navigation()
        data class NavigateToNextScreen(val screenRoute: String) : Navigation()

        object NavigateToAllCharactersScreen : Navigation()
    }
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val otpInteractor: OtpInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {
    override fun setInitialState(): OtpState = OtpState.getDefaultValues()

    override suspend fun handleEvents(event: OtpEvent) {
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
        }
    }

    private fun initView(userId: Int?) {
        if (userId == null) {
            // User id is null so go back to the previous screen by displaying that an unknown error occurred.
            setEffect {
                OtpEffect.Navigation.NavigateToPreviousScreenWithError(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
            }
            return
        }

        setState {
            copy(
                isLoading = false, // todo: change to true
                userId = userId
            )
        }
    }

    private fun onOtpFieldValueChange(otp: String) {
        if (otp.isDigitsOnly() && otp.length <= 6 && otp != currentState.otp) {
            setState {
                copy(
                    otp = otp
                )
            }
        }
    }

    private fun validateOtp() {
        // If the user entered OTP is of length 6, then proceed with its validation.
        if (currentState.otp.length == 6) {
            viewModelScope.launch {
                otpInteractor.getOtp(otpRequest = getOtpRequest())
                    .collect { getOtpPartialState ->
                        when (getOtpPartialState) {
                            is GetOtpPartialState.InternalServerError -> {
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

                            GetOtpPartialState.NoInternetConnection -> {
                                // No internet connection.
                                setState {
                                    copy(
                                        isLoading = false,
                                        hasInternetConnection = false
                                    )
                                }
                            }

                            is GetOtpPartialState.Success -> {
                                // OTPs matched.
                                setState {
                                    copy(
                                        isLoading = false,
                                    )
                                }

                                setEffect {
                                    OtpEffect.Navigation.NavigateToAllCharactersScreen
                                }
                            }

                            is GetOtpPartialState.ClientError -> {
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

                            is GetOtpPartialState.UserError -> {
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

    private fun getOtpValidationRules(): List<Rule> =
        listOf(
            Rule.ValidateNotBlank("OTP cannot be empty."),
            Rule.ValidateStringLength(6, "OTP must be 6 characters.")
        )

    private fun getOtpRequest(): OtpRequest =
        OtpRequest(
            userId = currentState.userId.toString(),
            otp = currentState.otp,
            epoch = DateUtil.getSecondsFromEpoch().toString()
        )
}
