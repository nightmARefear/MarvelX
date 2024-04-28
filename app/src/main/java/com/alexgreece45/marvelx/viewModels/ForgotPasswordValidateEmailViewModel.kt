package com.alexgreece45.marvelx.viewModels

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.models.ErrorButtonLayout
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The error codes of this screen.
 */
sealed class ForgotPasswordValidateEmailErrorCodes(val errorCode: String) {
    data object InitView : ForgotPasswordValidateEmailErrorCodes(errorCode = "FORGOT_PASSWORD_1000")
}

data class ForgotPasswordValidateEmailScreenErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val onClose: (() -> Unit)? = null,
    val errorButtonLayout: ErrorButtonLayout
)

sealed interface ForgotPasswordValidateEmailEvent : BaseEvent {
    data object OnBackClicked : ForgotPasswordValidateEmailEvent
    data class InitView(val requestIdEmail: String?) : ForgotPasswordValidateEmailEvent

    data object OnErrorCancelClick : ForgotPasswordValidateEmailEvent
    data object OnVerifyOtpButtonClicked : ForgotPasswordValidateEmailEvent
}

data class ForgotPasswordValidateEmailState(
    val isLoading: Boolean = false,

    val titleResId: Int = R.string.forgot_password_validate_email_screen_title,
    val subtitleResId: Int = R.string.forgot_password_validate_email_screen_subtitle,
    val stickyButtonResId: Int = R.string.forgot_password_validate_email_screen_sticky_button,

    val requestIdEmail: String = "",

    val isVerifyOtpButtonEnabled: Boolean = false,

    val error: ForgotPasswordValidateEmailScreenErrorView? = null
) : BaseState

sealed interface ForgotPasswordValidateEmailEffect : BaseEffect {
    sealed interface Navigation : ForgotPasswordValidateEmailEffect {
        data object PopBackStack : Navigation

        data class NavigateToPreviousScreenWithError(val errorMessage: String) : Navigation
        data class NavigateToForgotPasswordEnterPhoneNumberScreen(val requestIdEmail: String) :
            Navigation
    }
}

@HiltViewModel
class ForgotPasswordValidateEmailViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider
) :
    BaseViewModel<ForgotPasswordValidateEmailEvent, ForgotPasswordValidateEmailState, ForgotPasswordValidateEmailEffect>() {
    override fun setInitialState(): ForgotPasswordValidateEmailState =
        ForgotPasswordValidateEmailState()

    override fun handleEvents(event: ForgotPasswordValidateEmailEvent) {
        when (event) {
            is ForgotPasswordValidateEmailEvent.InitView -> {
                if (event.requestIdEmail != null) {
                    setState {
                        copy(
                            requestIdEmail = event.requestIdEmail
                        )
                    }
                    return
                }

                setState {
                    copy(
                        error = ForgotPasswordValidateEmailScreenErrorView(
                            errorTitle = errorViewTitle,
                            errorSubtitle = resourceProvider.getString(
                                R.string.generic_unknown_error_with_code,
                                ForgotPasswordValidateEmailErrorCodes.InitView.errorCode
                            ),
                            onClose = {
                                setEffect {
                                    ForgotPasswordValidateEmailEffect.Navigation.PopBackStack
                                }
                            },
                            errorButtonLayout = ErrorButtonLayout.Single(
                                primaryButtonLabel = resourceProvider.getString(R.string.forgot_password_validate_email_screen_go_to_previous_screen_button),
                                onPrimaryButtonClicked = {
                                    setEffect {
                                        ForgotPasswordValidateEmailEffect.Navigation.PopBackStack
                                    }
                                }
                            )
                        )
                    )
                }
            }

            ForgotPasswordValidateEmailEvent.OnBackClicked -> {
                setEffect {
                    ForgotPasswordValidateEmailEffect.Navigation.PopBackStack
                }
            }

            ForgotPasswordValidateEmailEvent.OnVerifyOtpButtonClicked -> TODO()

            ForgotPasswordValidateEmailEvent.OnErrorCancelClick -> {
                setEffect {
                    ForgotPasswordValidateEmailEffect.Navigation.PopBackStack
                }
            }
        }
    }

    private val errorViewTitle: String
        get() = resourceProvider.getString(R.string.forgot_password_validate_email_screen_error_view_title)
}