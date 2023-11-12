package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.otp.OtpDomain
import com.alexgreece45.marvelx.data.models.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseError
import com.alexgreece45.marvelx.data.models.otp.toOtpDomain
import com.alexgreece45.marvelx.data.models.otp.toOtpResponseDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface OtpInteractor {
    suspend fun validateOtp(otpRequest: OtpRequest): Flow<ValidateOtpPartialState>
}

class OtpInteractorImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : OtpInteractor {
    override suspend fun validateOtp(otpRequest: OtpRequest): Flow<ValidateOtpPartialState> = flow {
        api.validateOtp(otpRequest = otpRequest).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    emit(
                        ValidateOtpPartialState.Success(
                            otpDomain = responseBody.toOtpDomain()
                        )
                    )
                } ?: emit(
                    ValidateOtpPartialState.InternalServerError(
                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                    )
                )
            } else {
                // Error
                val deserializedError = gson.fromJson(
                    response.errorBody()?.string(),
                    OtpResponseError::class.java
                ).toOtpResponseDomain()

                when (deserializedError.errorCause) {
                    is OtpErrorCause.InternalServerError, is OtpErrorCause.Invalid -> {
                        emit(
                            ValidateOtpPartialState.InternalServerError(
                                errorMessage = deserializedError.errorCause.cause
                            )
                        )
                    }

                    OtpErrorCause.Expired, OtpErrorCause.InvalidLength, OtpErrorCause.NotMatch -> {
                        emit(
                            ValidateOtpPartialState.UserError(
                                otpErrorCause = deserializedError.errorCause
                            )
                        )
                    }
                }
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                ValidateOtpPartialState.NoInternetConnection
            }

            else -> {
                ValidateOtpPartialState.InternalServerError(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
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
     * Represents an internal server error. The displayed error message is "an internal server
     * error occurred".
     * */
    data class InternalServerError(val errorMessage: String) : ValidateOtpPartialState()

    /**
     * This data class is not used to display the actual error message to the screen.
     * The error stems from the app's code, rather than the user input.
     * The displayed error message is that an "unknown error occurred".
     * Furthermore, the contents of this data class are used by the app's logs for debugging.
     * */
    data class ClientError(val errorMessage: String) : ValidateOtpPartialState()


    /**
     * Represents if the error is due to invalid user-input.
     * */
    data class UserError(val otpErrorCause: OtpErrorCause) : ValidateOtpPartialState()


    /**
     * If there's no internet connection.
     * */
    data object NoInternetConnection : ValidateOtpPartialState()
}