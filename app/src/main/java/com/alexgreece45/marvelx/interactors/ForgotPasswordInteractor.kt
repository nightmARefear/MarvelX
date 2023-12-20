package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.domains.otp.OtpResponseErrorDomain
import com.alexgreece45.marvelx.data.models.otp.toOtpResponseErrorDomain
import com.alexgreece45.marvelx.repository.ForgotPasswordRepository
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositoryIsOtpBlockedEmailResponse
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositorySendOtpEmailResponse
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositorySendOtpPhoneNumberResponse
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositoryValidateOtpFromEmailResponse
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface ForgotPasswordInteractor {
    /**
     * Forgot password flow. This method is used to send an OTP to the given [email].
     *
     * If the request is successful, the `requestId` field will be later used to authenticate
     * this unique session of resetting a password.
     *
     * If the request fails because an OTP was requested too soon, the `requestId` field must be
     * preserved for future email OTP transmissions. Otherwise, the forgot password flow is
     * terminated as an another error has occurred.
     */
    suspend fun sendOtpToEmail(email: String): Flow<SendOtpToEmailPartialState>

    /**
     * Forgot password flow. This method is used to send an OTP to the given [phoneNumber].
     *
     * If the request is successful, the `requestId` field will be later used to authenticate this
     * unique session of resetting a password.
     *
     * If the request fails because an OTP was requested too soon, the `requestId` field must be
     * preserved for future phone number OTP transmissions. Otherwise, the forgot password flow is
     * terminated as an another error has occurred.
     */
    suspend fun sendOtpToPhoneNumber(phoneNumber: String): Flow<SendOtpToPhoneNumberPartialState>

    /**
     * Checks if the OTP functionality for the email is blocked.
     */
    suspend fun isOtpBlockedEmail(requestId: String): Flow<ForgotPasswordIsOtpBlockedEmailPartialState>

    /**
     * Checks if the OTP functionality for the phone number is blocked.
     */
    suspend fun isOtpBlockedPhoneNumber(requestId: String): Flow<ForgotPasswordIsOtpBlockedPhoneNumberPartialState>

    /**
     * Validates the given OTP against the one that was sent to the user's email address.
     *
     * @param requestId     The unique request ID.
     * @param otp           The OTP to be validated.
     */
    suspend fun validateOtpFromEmail(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordValidateOtpFromEmailPartialState>

    /**
     * Validates the given OTP against the one that was sent to the user's phone number.
     *
     * @param requestId     The unique request ID.
     * @param otp           The OTP to be validated.
     */
    suspend fun validateOtpFromPhoneNumber(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordValidateOtpFromPhoneNumberPartialState>
}

class ForgotPasswordInteractorImpl @Inject constructor(
    private val repository: ForgotPasswordRepository,
    private val resourceProvider: ResourceProvider
) : ForgotPasswordInteractor {
    override suspend fun sendOtpToEmail(email: String): Flow<SendOtpToEmailPartialState> = flow {
        repository.sendOtpToEmail(email = email).collect { sendOtpToEmailRepositoryPartialState ->
            when (sendOtpToEmailRepositoryPartialState) {
                is ForgotPasswordRepositorySendOtpEmailResponse.Failed -> {
                    emit(SendOtpToEmailPartialState.Failed(errorMessage = sendOtpToEmailRepositoryPartialState.errorMessage))
                }

                is ForgotPasswordRepositorySendOtpEmailResponse.Success -> {
                    sendOtpToEmailRepositoryPartialState.response?.let { response ->
                        emit(
                            SendOtpToEmailPartialState.Success(
                                requestId = response.requestId
                            )
                        )
                    }
                        ?: emit(SendOtpToEmailPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                SendOtpToEmailPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> (SendOtpToEmailPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
        }
    }

    override suspend fun sendOtpToPhoneNumber(phoneNumber: String): Flow<SendOtpToPhoneNumberPartialState> =
        flow {
            repository.sendOtpToPhoneNumber(phoneNumber = phoneNumber)
                .collect { sendOtpToPhoneNumberRepositoryPartialState ->
                    when (sendOtpToPhoneNumberRepositoryPartialState) {
                        is ForgotPasswordRepositorySendOtpPhoneNumberResponse.Failed -> {
                            emit(SendOtpToPhoneNumberPartialState.Failed(errorMessage = sendOtpToPhoneNumberRepositoryPartialState.errorMessage))
                        }

                        is ForgotPasswordRepositorySendOtpPhoneNumberResponse.Success -> {
                            sendOtpToPhoneNumberRepositoryPartialState.response?.let { response ->
                                emit(
                                    SendOtpToPhoneNumberPartialState.Success(
                                        requestId = response.requestId
                                    )
                                )
                            }
                                ?: emit(SendOtpToPhoneNumberPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
                        }
                    }
                }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    SendOtpToPhoneNumberPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> (SendOtpToPhoneNumberPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
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

    override suspend fun validateOtpFromEmail(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordValidateOtpFromEmailPartialState> = flow {
        repository.validateOtpFromEmail(requestId, otp).collect {
            when (it) {
                is ForgotPasswordRepositoryValidateOtpFromEmailResponse.Failed -> {
                    val otpResponseErrorDomain = it.otpResponseError.toOtpResponseErrorDomain()
                    when (otpResponseErrorDomain.errorCause) {
                        OtpErrorCause.Blocked -> {
                            if (otpResponseErrorDomain.blockExpiresIn == null) {
                                emit(
                                    ForgotPasswordValidateOtpFromEmailPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                                return@collect
                            }

                            emit(
                                ForgotPasswordValidateOtpFromEmailPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.Expired, OtpErrorCause.NotMatch -> {
                            emit(
                                ForgotPasswordValidateOtpFromEmailPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.InvalidLength, is OtpErrorCause.Invalid, OtpErrorCause.InternalServerError -> {
                            emit(
                                ForgotPasswordValidateOtpFromEmailPartialState.Failed(
                                    errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                )
                            )
                        }
                    }
                }

                is ForgotPasswordRepositoryValidateOtpFromEmailResponse.Success -> {
                    it.response?.let { response ->
                        emit(
                            ForgotPasswordValidateOtpFromEmailPartialState.Success(
                                isPhoneNumberRequired = response.isPhoneNumberRequired
                            )
                        )
                    } ?: emit(
                        ForgotPasswordValidateOtpFromEmailPartialState.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                ForgotPasswordValidateOtpFromEmailPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                ForgotPasswordValidateOtpFromEmailPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    override suspend fun validateOtpFromPhoneNumber(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordValidateOtpFromPhoneNumberPartialState> = flow {
        resourceProvider.getGenericErrorMessage()
        repository.validateOtpFromPhoneNumber(requestId, otp).collect {
            when (it) {
                is ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse.Failed -> {
                    val otpResponseErrorDomain = it.otpResponseError.toOtpResponseErrorDomain()
                    when (otpResponseErrorDomain.errorCause) {
                        OtpErrorCause.Blocked -> {
                            if (otpResponseErrorDomain.blockExpiresIn == null) {
                                emit(
                                    ForgotPasswordValidateOtpFromPhoneNumberPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                                return@collect
                            }

                            emit(
                                ForgotPasswordValidateOtpFromPhoneNumberPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.Expired, OtpErrorCause.NotMatch -> {
                            emit(
                                ForgotPasswordValidateOtpFromPhoneNumberPartialState.ClientError(
                                    otpResponseErrorDomain = otpResponseErrorDomain
                                )
                            )
                        }

                        OtpErrorCause.InvalidLength, is OtpErrorCause.Invalid, OtpErrorCause.InternalServerError -> {
                            emit(
                                ForgotPasswordValidateOtpFromPhoneNumberPartialState.Failed(
                                    errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                )
                            )
                        }
                    }
                }

                is ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse.Success -> {
                    it.response?.let { response ->
                        emit(
                            ForgotPasswordValidateOtpFromPhoneNumberPartialState.Success(
                                isEmailRequired = response.isEmailRequired
                            )
                        )
                    } ?: emit(
                        ForgotPasswordValidateOtpFromPhoneNumberPartialState.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                ForgotPasswordValidateOtpFromPhoneNumberPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                ForgotPasswordValidateOtpFromPhoneNumberPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }
}

sealed interface ForgotPasswordValidateOtpFromEmailPartialState {
    data class Success(val isPhoneNumberRequired: Boolean) :
        ForgotPasswordValidateOtpFromEmailPartialState

    data class Failed(val errorMessage: String) : ForgotPasswordValidateOtpFromEmailPartialState
    data class ClientError(val otpResponseErrorDomain: OtpResponseErrorDomain) :
        ForgotPasswordValidateOtpFromEmailPartialState
}

sealed interface ForgotPasswordValidateOtpFromPhoneNumberPartialState {
    data class Success(val isEmailRequired: Boolean) :
        ForgotPasswordValidateOtpFromPhoneNumberPartialState

    data class Failed(val errorMessage: String) :
        ForgotPasswordValidateOtpFromPhoneNumberPartialState

    data class ClientError(val otpResponseErrorDomain: OtpResponseErrorDomain) :
        ForgotPasswordValidateOtpFromPhoneNumberPartialState
}

sealed interface SendOtpToEmailPartialState {
    data class Success(val requestId: String) : SendOtpToEmailPartialState
    data class Failed(val errorMessage: String) : SendOtpToEmailPartialState
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