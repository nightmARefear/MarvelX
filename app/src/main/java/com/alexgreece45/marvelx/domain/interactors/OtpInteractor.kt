package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface OtpInteractor {
    suspend fun getOtp(otpRequest: OtpRequest): Flow<GetOtpPartialState>
}

class OtpInteractorImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider
) : OtpInteractor {
    override suspend fun getOtp(otpRequest: OtpRequest): Flow<GetOtpPartialState> = flow {
        api.postOtp(otpRequest = otpRequest).let { response ->
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    emit(
                        GetOtpPartialState.Success(
                            code = responseBody.code
                        )
                    )
                } ?: run {
                    emit(
                        GetOtpPartialState.Error(
                            errorMessage = resourceProvider.getString(R.string.internal_server_error)
                        )
                    )
                }
            } else {
                emit(
                    GetOtpPartialState.Error(
                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
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
                GetOtpPartialState.Error(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
            }
        }
    }
}


sealed class GetOtpPartialState {
    data class Success(val code: Int) : GetOtpPartialState()

    data class Error(val errorMessage: String) : GetOtpPartialState()

    object NoInternetConnection : GetOtpPartialState()
}