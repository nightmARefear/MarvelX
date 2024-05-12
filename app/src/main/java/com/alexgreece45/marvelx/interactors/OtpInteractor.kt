package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.data.domains.otp.OtpDomain
import com.alexgreece45.marvelx.data.domains.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.domains.otp.OtpResponseErrorDomain
import com.alexgreece45.marvelx.data.domains.resendOtp.ResponseResendOtpFailedDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.toOtpDomain
import com.alexgreece45.marvelx.data.models.otp.toOtpResponseErrorDomain
import com.alexgreece45.marvelx.data.models.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.data.models.resendOtp.toResponseResendOtpFailedDomain
import com.alexgreece45.marvelx.repository.OtpRepository
import com.alexgreece45.marvelx.repository.ResendOtpRepositoryResponse
import com.alexgreece45.marvelx.repository.ValidateOtpRepositoryResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface OtpInteractor {
    suspend fun validateOtp(otp: String, userId: Int): Flow<ValidateOtpPartialState>
    suspend fun resendOtp(
        userId: Int,
        verificationStep: VerificationStep
    ): Flow<ResendOtpPartialState>
}

class OtpInteractorImpl @Inject constructor(
    private val repository: OtpRepository,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLoggerController
) : OtpInteractor {
    override suspend fun validateOtp(otp: String, userId: Int): Flow<ValidateOtpPartialState> =
        flow {
            val otpRequest = OtpRequest(
                userId = userId.toString(),
                otp = otp
            )
            repository.validateOtp(otpRequest).collect {
                when (it) {
                    is ValidateOtpRepositoryResponse.Failed -> {
                        val otpResponseErrorDomain = it.otpResponseError.toOtpResponseErrorDomain()
                        when (otpResponseErrorDomain.errorCause) {
                            // Server errors.
                            is OtpErrorCause.Invalid, OtpErrorCause.NotFound, OtpErrorCause.InternalServerError -> {
                                emit(
                                    ValidateOtpPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                            }

                            // Client errors.
                            is OtpErrorCause.Blocked -> {
                                // Check if `blockExpiresIn` field exists. If it does, emit `ClientError`
                                // with the corresponding response error domain, otherwise emit `Failed`
                                // with an internal server error as its error message.
                                if (otpResponseErrorDomain.blockExpiresIn == null) {
                                    emit(
                                        ValidateOtpPartialState.Failed(
                                            errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                        )
                                    )
                                    return@collect
                                }

                                // `blockExpiresIn` is NOT null
                                emit(
                                    ValidateOtpPartialState.ClientError(
                                        otpResponseErrorDomain = otpResponseErrorDomain
                                    )
                                )
                            }

                            is OtpErrorCause.Expired, OtpErrorCause.NotMatch, OtpErrorCause.InvalidLength -> {
                                emit(
                                    ValidateOtpPartialState.ClientError(
                                        otpResponseErrorDomain = otpResponseErrorDomain
                                    )
                                )
                            }
                        }
                    }

                    is ValidateOtpRepositoryResponse.Success -> {
                        it.response?.let { otpResponseSuccess ->
                            emit(
                                ValidateOtpPartialState.Success(
                                    otpDomain = otpResponseSuccess.toOtpDomain()
                                )
                            )
                        } ?: emit(
                            ValidateOtpPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(validateOtp) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    ValidateOtpPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    ValidateOtpPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage())
                }
            }
        }

    override suspend fun resendOtp(
        userId: Int,
        verificationStep: VerificationStep
    ): Flow<ResendOtpPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val requestResendOtp =
            RequestResendOtp(userId = userId, verificationStep = verificationStep.key)
        repository.resendOtp(requestResendOtp).collect {
            when (it) {
                is ResendOtpRepositoryResponse.Failed -> {
                    emit(
                        ResendOtpPartialState.Failed(
                            responseResendOtpFailedDomain = it.responseResendOtpFailed.toResponseResendOtpFailedDomain()
                        )
                    )
                }

                is ResendOtpRepositoryResponse.Success -> {
                    it.responseResendOtpSuccess?.let { responseResendOtpSuccess ->
                        emit(
                            ResendOtpPartialState.Success(
                                expiresIn = responseResendOtpSuccess.expiresIn
                            )
                        )
                    } ?: emit(
                        ResendOtpPartialState.Error(
                            errorMessage = genericErrorMessage
                        )
                    )
                }
            }
        }
    }.safeAsync {
        logger.e { "(resendOtp) error: ${it.message.orEmpty()}" }
        when (it) {
            is IOException -> {
                ResendOtpPartialState.Error(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                ResendOtpPartialState.Error(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }
}

sealed class ValidateOtpPartialState {
    /**
     * The OTP was correct. User is navigated to the next screen as defined in the viewModel.
     * */
    data class Success(val otpDomain: OtpDomain) : ValidateOtpPartialState()

    /**
     * Indicates that a server-side or an in-app error has occurred. This either means that the
     * [OtpErrorCause] was [OtpErrorCause.InternalServerError] or [OtpErrorCause.Invalid], OR
     * that something else has gone wrong client-side (i.e., no internet connection, etc.).
     * */
    data class Failed(val errorMessage: String) : ValidateOtpPartialState()

    /**
     * Indicates that the error is due to invalid user-input. This error is displayed as-is on the
     * screen.
     * */
    data class ClientError(val otpResponseErrorDomain: OtpResponseErrorDomain) :
        ValidateOtpPartialState()
}

sealed class ResendOtpPartialState {
    /**
     * The OTP was successfully resent.
     */
    data class Success(val expiresIn: Long) : ResendOtpPartialState()

    /**
     * Indicates that either a server-side or an in-app error has occurred.
     */
    data class Failed(val responseResendOtpFailedDomain: ResponseResendOtpFailedDomain) :
        ResendOtpPartialState()

    /**
     * A general error occurred.
     *
     * This error is shown in a dialog / snackbar / toast.
     */
    data class Error(val errorMessage: String) :
        ResendOtpPartialState()
}