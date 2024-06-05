package com.alexgreece45.marvelx.ui.screens.changePassword

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.ChangePasswordInteractor
import com.alexgreece45.marvelx.domain.model_api.otp.OtpFlowType
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.models.ErrorButtonLayout
import com.alexgreece45.marvelx.util.ResourceProvider

/**
 * All the error codes of the `ChangePasswordScreen` for error reporting by the users.
 */
private sealed class ChangePasswordScreenErrorCodes(val code: String) {
    sealed class InitChecks(code: String) : ChangePasswordScreenErrorCodes(code) {
        data object UnexpectedError : ChangePasswordScreenErrorCodes("CHANGE_PASSWORD_1000")
        data object NullableFields : ChangePasswordScreenErrorCodes("CHANGE_PASSWORD_1001")
        data object InvalidOtpFlowType : ChangePasswordScreenErrorCodes("CHANGE_PASSWORD_1002")
    }
}

interface ChangePasswordContract {
    sealed interface Event : BaseEvent {
        data class Init(
            val otpFlowType: String?,
            val requestIdEmail: String?,
            val requestIdPhoneNumber: String?
        ) : Event
    }

    data class State(
        val otpFlowType: OtpFlowType? = null,
        val requestIdEmail: String = "",
        val requestIdPhoneNumber: String = "",

        val error: ChangePasswordScreenErrorView? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
        }
    }
}

class ChangePasswordViewModel(
    private val resourceProvider: ResourceProvider,
    private val changePasswordInteractor: ChangePasswordInteractor
) :
    BaseViewModel<ChangePasswordContract.Event, ChangePasswordContract.State, ChangePasswordContract.Effect>() {
    override fun setInitialState(): ChangePasswordContract.State =
        ChangePasswordContract.State()

    override fun handleEvents(event: ChangePasswordContract.Event) {
        when (event) {
            is ChangePasswordContract.Event.Init -> {
                when (OtpFlowType.getType(event.otpFlowType.orEmpty())) {
                    is OtpFlowType.Invalid -> {
                        showGenericErrorViewWithErrorCode(
                            errorCode = ChangePasswordScreenErrorCodes.InitChecks.InvalidOtpFlowType,

                            )
                    }

                    OtpFlowType.ForgotPassword.Email -> TODO()
                    OtpFlowType.ForgotPassword.PhoneNumber -> TODO()
                    OtpFlowType.Register -> TODO()
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
        errorCode: ChangePasswordScreenErrorCodes
    ) {
        showGenericErrorViewWithErrorMessage(
            errorMessage = resourceProvider.getString(
                R.string.otp_screen_generic_error_subtitle_with_error_code,
                errorCode.code
            ),
            otpFlowType = OtpFlowType.ForgotPassword.Email // or OtpFlowType.ForgotPassword.PhoneNumber
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
                error = ChangePasswordScreenErrorView(
                    errorTitle = resourceProvider.getString(R.string.otp_screen_generic_error_title),
                    errorSubtitle = errorMessage,
                    onCloseClicked = {
                        setEffect {
                            ChangePasswordContract.Effect.Navigation.PopBackStack
                        }
                    },
                    errorButtonLayout = ErrorButtonLayout.Single(
                        primaryButtonLabel = when (otpFlowType) {
                            is OtpFlowType.Invalid, OtpFlowType.ForgotPassword.Email, OtpFlowType.ForgotPassword.PhoneNumber -> resourceProvider.getString(
                                R.string.generic_go_to_previous_screen_button
                            )

                            OtpFlowType.Register -> resourceProvider.getString(R.string.otp_screen_back_to_login_button)
                        },
                        onPrimaryButtonClicked = {
                            setEffect {
                                ChangePasswordContract.Effect.Navigation.PopBackStack
                            }
                        }
                    )
                )
            )
        }
    }
}