package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.models_api.otp.OtpErrorCause
import com.alexgreece45.marvelx.domain.models_api.otp.OtpResponseErrorDomain
import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.network_logic.models_api.otp.toOtpResponseErrorDomain
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepository
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositoryIsOtpBlockedEmailResponse
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositorySendOtpResponse
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositoryValidateOtpFromEmailResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

interface ForgotPasswordInteractor {
    /**
     * Forgot password flow. This method is used to send an OTP to the given [data] based on the
     * [verificationStep].
     *
     * If the request is successful, the `requestId` field will be later used to authenticate
     * this unique session of resetting a password.
     *
     * If the request fails because an OTP was requested too soon, the `requestId` field must be
     * preserved for future email/phone number OTP transmissions. Otherwise, the forgot password
     * flow is terminated as another error has occurred.
     */
    suspend fun sendOtp(
        data: String,
        verificationStep: VerificationStep
    ): Flow<ForgotPasswordSendOtpPartialState>

    /**
     * Checks if the OTP functionality for the email is blocked.
     */
    suspend fun isOtpBlockedEmail(requestId: String): Flow<ForgotPasswordIsOtpBlockedEmailPartialState>

    /**
     * Checks if the OTP functionality for the phone number is blocked.
     */
    suspend fun isOtpBlockedPhoneNumber(requestId: String): Flow<ForgotPasswordIsOtpBlockedPhoneNumberPartialState>

    /**
     * Validates the given OTP against the one that was sent to the user's email address / phone
     * based on the provided [requestId].
     *
     * @param requestId         The unique request ID.
     * @param otp               The OTP to be validated.
     * @param verificationStep  The verification step; either [VerificationStep.OtpEmail] or [VerificationStep.OtpPhoneNumber].
     */
    suspend fun validateOtp(
        requestId: String,
        otp: String,
        verificationStep: VerificationStep
    ): Flow<ForgotPasswordValidateOtpPartialState>
}

class ForgotPasswordInteractorImpl(
    private val repository: ForgotPasswordRepository,
    private val resourceProvider: ResourceProvider
) : ForgotPasswordInteractor {
    override suspend fun sendOtp(
        data: String,
        verificationStep: VerificationStep
    ): Flow<ForgotPasswordSendOtpPartialState> = flow {
        repository.sendOtp(
            data = data,
            verificationStep = verificationStep.key
        ).collect { sendOtpToEmailRepositoryPartialState ->
            when (sendOtpToEmailRepositoryPartialState) {
                is ForgotPasswordRepositorySendOtpResponse.Failed -> {
                    emit(ForgotPasswordSendOtpPartialState.Failed(errorMessage = sendOtpToEmailRepositoryPartialState.errorMessage))
                }

                is ForgotPasswordRepositorySendOtpResponse.Success -> {
                    sendOtpToEmailRepositoryPartialState.response?.let { response ->
                        emit(
                            ForgotPasswordSendOtpPartialState.Success(
                                requestId = response.requestId
                            )
                        )
                    }
                        ?: emit(ForgotPasswordSendOtpPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                ForgotPasswordSendOtpPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> (ForgotPasswordSendOtpPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
        }
    }

    override suspend fun isOtpBlockedEmail(requestId: String): Flow<ForgotPasswordIsOtpBlockedEmailPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.isOtpBlockedEmail(requestId).collect { repositoryResult ->
                when (repositoryResult) {
                    is ForgotPasswordRepositoryIsOtpBlockedEmailResponse.Failed -> {
                        emit(
                            ForgotPasswordIsOtpBlockedEmailPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }

                    is ForgotPasswordRepositoryIsOtpBlockedEmailResponse.Success -> {
                        repositoryResult.response?.let { response ->
                            if (response.isOtpBlocked) {
                                emit(
                                    ForgotPasswordIsOtpBlockedEmailPartialState.BlockedEmail(
                                        otpBlocksExpiresIn = response.otpBlockExpiresIn,
                                        otpInterval = response.otpInterval
                                    )
                                )
                            } else {
                                emit(
                                    ForgotPasswordIsOtpBlockedEmailPartialState.NotBlockedEmail(
                                        otpInterval = response.otpInterval
                                    )
                                )
                            }
                        } ?: emit(
                            ForgotPasswordIsOtpBlockedEmailPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    ForgotPasswordIsOtpBlockedEmailPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    ForgotPasswordIsOtpBlockedEmailPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun isOtpBlockedPhoneNumber(requestId: String): Flow<ForgotPasswordIsOtpBlockedPhoneNumberPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.isOtpBlockedPhoneNumber(requestId).collect { repositoryResult ->
                when (repositoryResult) {
                    is ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse.Failed -> {
                        emit(
                            ForgotPasswordIsOtpBlockedPhoneNumberPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }

                    is ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse.Success -> {
                        repositoryResult.response?.let { response ->
                            if (response.isOtpBlocked) {
                                emit(
                                    ForgotPasswordIsOtpBlockedPhoneNumberPartialState.BlockedPhoneNumber(
                                        otpBlocksExpiresIn = response.otpBlockExpiresIn,
                                        otpInterval = response.otpInterval
                                    )
                                )
                            } else {
                                emit(
                                    ForgotPasswordIsOtpBlockedPhoneNumberPartialState.NotBlockedPhoneNumber(
                                        otpInterval = response.otpInterval
                                    )
                                )
                            }
                        } ?: emit(
                            ForgotPasswordIsOtpBlockedPhoneNumberPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    ForgotPasswordIsOtpBlockedPhoneNumberPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    ForgotPasswordIsOtpBlockedPhoneNumberPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun validateOtp(
        requestId: String,
        otp: String,
        verificationStep: VerificationStep
    ): Flow<ForgotPasswordValidateOtpPartialState> = flow {
        repository.validateOtp(requestId, otp, verificationStep.key).collect {
            when (it) {
                is ForgotPasswordRepositoryValidateOtpFromEmailResponse.Failed -> {
                    val otpResponseErrorDomain = it.otpResponseError.toOtpResponseErrorDomain()
                    when (otpResponseErrorDomain.errorCause) {
                        OtpErrorCause.Blocked -> {
                            if (otpResponseErrorDomain.blockExpiresIn == null) {
                                emit(
                                    ForgotPasswordValidateOtpPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                                return@collect
                            }

                            emit(
                                ForgotPasswordValidateOtpPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.Expired, OtpErrorCause.NotMatch -> {
                            emit(
                                ForgotPasswordValidateOtpPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.NotFound, OtpErrorCause.InvalidLength, is OtpErrorCause.Invalid, OtpErrorCause.InternalServerError -> {
                            emit(
                                ForgotPasswordValidateOtpPartialState.Failed(
                                    errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                )
                            )
                        }
                    }
                }

                is ForgotPasswordRepositoryValidateOtpFromEmailResponse.Success -> {
                    it.response?.let { response ->
                        emit(
                            ForgotPasswordValidateOtpPartialState.Success(
                                isPhoneNumberRequired = response.isPhoneNumberRequired
                            )
                        )
                    } ?: emit(
                        ForgotPasswordValidateOtpPartialState.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                ForgotPasswordValidateOtpPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                ForgotPasswordValidateOtpPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }
}

sealed interface ForgotPasswordValidateOtpPartialState {
    data class Success(val isPhoneNumberRequired: Boolean) :
        ForgotPasswordValidateOtpPartialState

    data class Failed(val errorMessage: String) : ForgotPasswordValidateOtpPartialState
    data class ClientError(val otpResponseErrorDomain: OtpResponseErrorDomain) :
        ForgotPasswordValidateOtpPartialState
}

sealed interface ForgotPasswordSendOtpPartialState {
    data class Success(val requestId: String) : ForgotPasswordSendOtpPartialState
    data class Failed(val errorMessage: String) : ForgotPasswordSendOtpPartialState
}

sealed interface SendOtpToPhoneNumberPartialState {
    data class Success(val requestId: String) : SendOtpToPhoneNumberPartialState
    data class Failed(val errorMessage: String) : SendOtpToPhoneNumberPartialState
}

sealed interface ForgotPasswordIsOtpBlockedEmailPartialState {
    data class BlockedEmail(val otpBlocksExpiresIn: Long, val otpInterval: Long) :
        ForgotPasswordIsOtpBlockedEmailPartialState

    data class NotBlockedEmail(val otpInterval: Long) : ForgotPasswordIsOtpBlockedEmailPartialState

    data class Failed(val errorMessage: String) : ForgotPasswordIsOtpBlockedEmailPartialState
}

sealed interface ForgotPasswordIsOtpBlockedPhoneNumberPartialState {
    data class BlockedPhoneNumber(val otpBlocksExpiresIn: Long, val otpInterval: Long) :
        ForgotPasswordIsOtpBlockedPhoneNumberPartialState

    data class NotBlockedPhoneNumber(val otpInterval: Long) :
        ForgotPasswordIsOtpBlockedPhoneNumberPartialState

    data class Failed(val errorMessage: String) : ForgotPasswordIsOtpBlockedPhoneNumberPartialState
}