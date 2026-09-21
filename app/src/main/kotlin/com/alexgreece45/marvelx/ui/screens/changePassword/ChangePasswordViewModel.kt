package com.alexgreece45.marvelx.ui.screens.changePassword

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.domain.interactors.ChangePasswordInteractor
import com.alexgreece45.marvelx.domain.interactors.ChangePasswordPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import com.alexgreece45.marvelx.util.constants.ConstantsInputField
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

enum class PasswordStrength { NONE, WEAK, FAIR, GOOD, STRONG }

interface ChangePasswordContract {
    sealed interface Event : BaseEvent {
        data class Init(val requestIdEmail: String?) : Event
        data class OnPasswordChanged(val value: String) : Event
        data class OnConfirmPasswordChanged(val value: String) : Event
        data object OnTogglePasswordVisibility : Event
        data object OnToggleConfirmPasswordVisibility : Event
        data object OnSubmitClicked : Event
        data object OnBackClicked : Event
    }

    data class State(
        val isLoading: Boolean = false,
        val requestIdEmail: String = "",

        val password: String = "",
        val confirmPassword: String = "",
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,

        val passwordValidation: FormValidationResult = FormValidationResult(isValid = false),
        val confirmPasswordValidation: FormValidationResult = FormValidationResult(isValid = false),
        val strength: PasswordStrength = PasswordStrength.NONE,

        val error: ChangePasswordScreenErrorView? = null
    ) : BaseState {
        val isSubmitEnabled: Boolean
            get() = !isLoading && passwordValidation.isValid && confirmPasswordValidation.isValid
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
            data object GoToPasswordChangedScreen : Navigation
        }
    }
}

@KoinViewModel(binds = [ChangePasswordViewModel::class])
class ChangePasswordViewModel(
    private val resourceProvider: ResourceProvider,
    private val changePasswordInteractor: ChangePasswordInteractor,
    private val formValidator: FormValidator,
    private val fieldValidationRules: FieldValidationRules,
    snackbarDelegate: SnackbarDelegate
) : BaseViewModel<ChangePasswordContract.Event, ChangePasswordContract.State, ChangePasswordContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    override fun setInitialState(): ChangePasswordContract.State = ChangePasswordContract.State()

    override fun handleEvents(event: ChangePasswordContract.Event) {
        when (event) {
            is ChangePasswordContract.Event.Init -> init(event.requestIdEmail)

            is ChangePasswordContract.Event.OnPasswordChanged -> {
                setState { copy(password = event.value) }
                validatePassword()
                validateConfirmPassword()
            }

            is ChangePasswordContract.Event.OnConfirmPasswordChanged -> {
                setState { copy(confirmPassword = event.value) }
                validateConfirmPassword()
            }

            ChangePasswordContract.Event.OnTogglePasswordVisibility -> {
                setState { copy(isPasswordVisible = !isPasswordVisible) }
            }

            ChangePasswordContract.Event.OnToggleConfirmPasswordVisibility -> {
                setState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }
            }

            ChangePasswordContract.Event.OnSubmitClicked -> submit()

            ChangePasswordContract.Event.OnBackClicked -> {
                setEffect { ChangePasswordContract.Effect.Navigation.PopBackStack }
            }
        }
    }

    private fun init(requestIdEmail: String?) {
        if (requestIdEmail.isNullOrBlank()) {
            showInvalidRequestError()
            return
        }
        setState { copy(requestIdEmail = requestIdEmail) }
    }

    private fun validatePassword() {
        val rules = fieldValidationRules.getPasswordValidationRules() + listOf(
            Rule.ValidateRegex(
                regex = UPPERCASE_REGEX,
                errorMessage = resourceProvider.getString(R.string.screen_change_password_error_uppercase)
            ),
            Rule.ValidateRegex(
                regex = LOWERCASE_REGEX,
                errorMessage = resourceProvider.getString(R.string.screen_change_password_error_lowercase)
            ),
            Rule.ValidateRegex(
                regex = DIGIT_REGEX,
                errorMessage = resourceProvider.getString(R.string.screen_change_password_error_digit)
            )
        )
        val result = formValidator.validateForm(Form(mapOf(rules to currentState.password)))

        setState {
            copy(
                passwordValidation = result,
                strength = password.toStrength()
            )
        }
    }

    private fun validateConfirmPassword() {
        val rules = listOf(
            Rule.ValidateNotBlank(
                errorMessage = resourceProvider.getString(R.string.validation_rule_error_blank_password)
            ),
            Rule.ValidateStringMatch(
                stringToMatch = currentState.password,
                errorMessage = resourceProvider.getString(R.string.screen_change_password_error_mismatch),
                isCaseSensitive = true
            )
        )
        setState {
            copy(confirmPasswordValidation = formValidator.validateForm(Form(mapOf(rules to confirmPassword))))
        }
    }

    private fun submit() {
        if (!currentState.isSubmitEnabled) return

        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val result = changePasswordInteractor.resetPassword(
                requestId = currentState.requestIdEmail,
                newPassword = currentState.password
            )
            setState { copy(isLoading = false) }

            when (result) {
                ChangePasswordPartialState.Success -> {
                    setEffect { ChangePasswordContract.Effect.Navigation.GoToPasswordChangedScreen }
                }

                ChangePasswordPartialState.NoInternetConnection -> {
                    showErroneousSnackbar(errorMessage = resourceProvider.getNoInternetConnectionMessage())
                }

                is ChangePasswordPartialState.Failed -> {
                    showErroneousSnackbar(
                        errorMessage = resourceProvider.getString(R.string.screen_change_password_snackbar_error)
                    )
                }
            }
        }
    }

    private fun showInvalidRequestError() {
        setState {
            copy(
                error = ChangePasswordScreenErrorView(
                    errorTitle = resourceProvider.getString(R.string.otp_screen_generic_error_title),
                    errorSubtitle = resourceProvider.getString(R.string.screen_change_password_error_invalid_request),
                    onCloseClicked = {
                        setEffect { ChangePasswordContract.Effect.Navigation.PopBackStack }
                    },
                    errorButtonLayout = ErrorButtonLayout.Single(
                        primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                        onPrimaryButtonClicked = {
                            setEffect { ChangePasswordContract.Effect.Navigation.PopBackStack }
                        }
                    )
                )
            )
        }
    }

    /**
     * A coarse hint for the user, deliberately independent of the hard validation rules.
     */
    private fun String.toStrength(): PasswordStrength {
        if (isBlank()) return PasswordStrength.NONE
        var score = 0
        if (length >= ConstantsInputField.Password.minLength) score++
        if (UPPERCASE_REGEX.containsMatchIn(this) && LOWERCASE_REGEX.containsMatchIn(this)) score++
        if (DIGIT_REGEX.containsMatchIn(this)) score++
        if (SYMBOL_REGEX.containsMatchIn(this)) score++
        return when (score) {
            0, 1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.FAIR
            3 -> PasswordStrength.GOOD
            else -> PasswordStrength.STRONG
        }
    }

    private companion object {
        val UPPERCASE_REGEX = Regex("[A-Z]")
        val LOWERCASE_REGEX = Regex("[a-z]")
        val DIGIT_REGEX = Regex("[0-9]")
        val SYMBOL_REGEX = Regex("[^A-Za-z0-9]")
    }
}
