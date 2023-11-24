package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.domains.otp.OtpDomain
import com.alexgreece45.marvelx.data.domains.otp.OtpResponseErrorDomain
import com.alexgreece45.marvelx.data.models.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.toOtpResponseErrorDomain
import com.alexgreece45.marvelx.data.models.otp.toOtpDomain
import com.alexgreece45.marvelx.repository.OtpRepository
import com.alexgreece45.marvelx.repository.ValidateOtpRepositoryResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.LogPriority
import logcat.logcat
import okio.IOException
import javax.inject.Inject

interface OtpInteractor {
    suspend fun validateOtp(otp: String): Flow<ValidateOtpPartialState>
}

class OtpInteractorImpl @Inject constructor(
    private val repository: OtpRepository,
    private val resourceProvider: ResourceProvider,
    private val prefKeys: PrefKeys
) : OtpInteractor {
    override suspend fun validateOtp(otp: String): Flow<ValidateOtpPartialState> = flow {
        val otpRequest = OtpRequest(
            userId = prefKeys.getUserId().toString(),
            otp = otp
        )
        repository.validateOtp(otpRequest).collect {
            when (it) {
                is ValidateOtpRepositoryResponse.Error -> {
                    emit(ValidateOtpPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
                }

                is ValidateOtpRepositoryResponse.Failed -> {
                    val otpResponseErrorDomain = it.otpResponseError.toOtpResponseErrorDomain()
                    when (otpResponseErrorDomain.errorCause) {
                        // Server errors.
                        is OtpErrorCause.Invalid, OtpErrorCause.InternalServerError -> {
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
                            //TODO: TO BE UNCOMMENTED
                            /*if (otpResponseErrorDomain.blockExpiresIn == null) {
                                emit(
                                    ValidateOtpPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                                return@collect
                            }*/

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
        logcat(LogPriority.ERROR) { "(validateOtp) error: ${it.message.orEmpty()}" }
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
     * Indicates that the error is due to invalid user-input.
     * */
    data class ClientError(val otpResponseErrorDomain: OtpResponseErrorDomain) :
        ValidateOtpPartialState()
}