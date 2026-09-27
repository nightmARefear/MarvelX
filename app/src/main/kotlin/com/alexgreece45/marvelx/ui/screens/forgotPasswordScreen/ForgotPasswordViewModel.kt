package com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordInteractor
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordSendOtpPartialState
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.EmailMasker
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.constants.ConstantsInputField
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * First step of the forgot-password flow: collect the email and request an OTP. OTP entry and the
 * new-password form live on their own screens (`OtpScreen`, `ChangePasswordScreen`).
 */
sealed class ForgotPasswordEvent : BaseEvent {
    data class OnEmailValueChange(val email: String) : ForgotPasswordEvent()
    data object OnBackClick : ForgotPasswordEvent()
    data object OnEmailDoneClick : ForgotPasswordEvent()
    data object OnContinueButtonClicked : ForgotPasswordEvent()
}

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val email: String = "",
    val isContinueButtonEnabled: Boolean = false,
    val emailValidationResult: FormValidationResult = FormValidationResult(isValid = false)
) : BaseState

sealed class ForgotPasswordEffect : BaseEffect {
    sealed class Navigation : ForgotPasswordEffect() {
        data object PopToLoginScreen : Navigation()
        data class NavigateToLoginScreenWithError(val errorMessage: String) : Navigation()
        data class NavigateToOtpScreen(
            val requestIdEmail: String,
            val maskedEmail: String?
        ) : Navigation()
    }
}

@KoinViewModel
class ForgotPasswordViewModel(
    private val forgotPasswordInteractor: ForgotPasswordInteractor,
    private val resourceProvider: ResourceProvider,
    private val formValidator: FormValidator
) : BaseViewModel<ForgotPasswordEvent, ForgotPasswordState, ForgotPasswordEffect>() {

    override fun setInitialState(): ForgotPasswordState = ForgotPasswordState()

    override fun handleEvents(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.OnEmailValueChange -> {
                setState { copy(email = event.email) }
                validateEmail()
            }

            ForgotPasswordEvent.OnEmailDoneClick,
            ForgotPasswordEvent.OnContinueButtonClicked -> {
                if (currentState.isContinueButtonEnabled && !currentState.isLoading) {
                    sendOtpToEmail()
                }
            }

            ForgotPasswordEvent.OnBackClick -> {
                setEffect { ForgotPasswordEffect.Navigation.PopToLoginScreen }
            }
        }
    }

    private fun sendOtpToEmail() {
        setState { copy(isLoading = true) }

        // The email the OTP will be sent to.
        val email = currentState.email

        viewModelScope.launch {
            forgotPasswordInteractor.sendOtp(
                data = email,
                verificationStep = VerificationStep.OtpEmail
            ).collect { partialState ->
                when (partialState) {
                    is ForgotPasswordSendOtpPartialState.Failed -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            ForgotPasswordEffect.Navigation.NavigateToLoginScreenWithError(
                                errorMessage = resourceProvider.getGenericErrorMessage()
                            )
                        }
                    }

                    is ForgotPasswordSendOtpPartialState.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            ForgotPasswordEffect.Navigation.NavigateToOtpScreen(
                                requestIdEmail = partialState.requestId,
                                maskedEmail = EmailMasker.mask(email)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateEmail() {
        val result = formValidator.validateForm(Form(mapOf(getEmailValidationRules() to currentState.email)))
        setState {
            copy(
                emailValidationResult = result,
                isContinueButtonEnabled = result.isValid
            )
        }
    }

    private fun getEmailValidationRules(): List<Rule> = listOf(
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
