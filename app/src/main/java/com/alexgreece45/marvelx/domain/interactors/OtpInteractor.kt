package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.otp.OtpErrorCause
import com.alexgreece45.marvelx.data.models.otp.OtpResponseError
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.toDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import javax.inject.Inject

interface OtpInteractor {
    suspend fun getOtp(otpRequest: OtpRequest): Flow<GetOtpPartialState>
}

class OtpInteractorImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : OtpInteractor {
    override suspend fun getOtp(otpRequest: OtpRequest): Flow<GetOtpPartialState> = flow {
        api.validateOtp(otpRequest = otpRequest).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    emit(
                        GetOtpPartialState.Success(
                            token = responseBody.token
                        )
                    )
                } ?: run {
                    emit(
                        GetOtpPartialState.InternalServerError(
                            errorMessage = resourceProvider.getString(R.string.internal_server_error)
                        )
                    )
                }
            } else {
                // Error
                val deserializedError = gson.fromJson(
                    (response.errorBody() as ResponseBody).string(),
                    OtpResponseError::class.java
                ).toDomain()

                emit(
                    GetOtpPartialState.UserError(
                        otpErrorCause = deserializedError.errorCause
                    )
                )
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                GetOtpPartialState.NoInternetConnection
            }

            else -> {
                GetOtpPartialState.InternalServerError(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
            }
        }
    }
}


sealed class GetOtpPartialState {
    /**
     * The OTP was correct. User is navigated to the next screen as defined in the viewModel.
     * */
    data class Success(val token: String?) : GetOtpPartialState()

    /**
     * Represents an internal server error. The displayed error message is "an internal server
     * error occurred".
     * */
    data class InternalServerError(val errorMessage: String) : GetOtpPartialState()

    /**
     * This data class is not used to display the actual error message to the screen.
     * The error stems from the app's code, rather than the user input.
     * The displayed error message is that an "unknown error occurred".
     * Furthermore, the contents of this data class are used by the app's logs for debugging.
     * */
    data class ClientError(val errorMessage: String) : GetOtpPartialState()


    /**
     * Represents if the error is due to invalid user-input.
     * */
    data class UserError(val otpErrorCause: OtpErrorCause) : GetOtpPartialState()


    /**
     * When there is no internet connection.
     * */
    object NoInternetConnection : GetOtpPartialState()
}