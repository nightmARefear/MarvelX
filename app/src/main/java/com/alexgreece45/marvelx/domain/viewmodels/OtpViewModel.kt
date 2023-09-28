package com.alexgreece45.marvelx.domain.viewmodels

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
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidator
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
    val userEnteredOtp: String = "",
    /**
     * The actual OTP (retrieved from the MarvelX-Backend).
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
                userId = null,
                userEnteredOtp = ""
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
    private val formValidator: FormValidator,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<OtpEvent, OtpState, OtpEffect>() {
    override fun setInitialState(): OtpState = OtpState.getDefaultValues()

    override suspend fun handleEvents(event: OtpEvent) {
        when (event) {
            is OtpEvent.InitView -> {
                initView(event.userId)
            }

            is OtpEvent.OnOtpFieldValueChange -> {
                validateOtp(event.otp)
            }

            OtpEvent.OnDoneClick -> {
                validateOtp(currentState.userEnteredOtp)
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
                isLoading = true,
                userId = userId
            )
        }

        // Get OTP.
        viewModelScope.launch {
            otpInteractor.getOtp(otpRequest = getOtpRequest()).collect { getOtpPartialState ->
                when (getOtpPartialState) {
                    is GetOtpPartialState.Error -> {
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
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
                            )
                        }
                    }

                    is GetOtpPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                otp = getOtpPartialState.code
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateOtp(otp: String) {
        if (otp != currentState.userEnteredOtp) {
            setState {
                copy(
                    userEnteredOtp = otp
                )
            }
        }
        // If the user entered OTP is of length 6, then proceed with its validation.
        if (otp.length == 6) {
            viewModelScope.launch {
                formValidator.validateForms(listOf(Form(mapOf(getOtpValidationRules() to otp))))
                    .collect { formsValidationResult ->
                        setState {
                            copy(
                                enteredOtpValidationResult = formsValidationResult
                            )
                        }

                        // If the user-entered OTP is the same as the actual OTP, then proceed to login the user.
                        if (currentState.enteredOtpValidationResult.isValid) {
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
            Rule.ValidateStringLength(6, "OTP must be 6 characters."),
            Rule.ValidateStringMatch(
                currentState.otp,
                "The OTP that you have entered is incorrect."
            )
        )

    private fun getOtpRequest(): OtpRequest =
        OtpRequest(
            userId = currentState.userId ?: -1,
            otp = currentState.userEnteredOtp.toInt(),
            epoch = DateUtil.getTimestamp()
        )
}
