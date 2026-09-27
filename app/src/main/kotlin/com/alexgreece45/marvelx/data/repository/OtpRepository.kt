package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.otp.OtpRequest
import com.alexgreece45.marvelx.core.network.model.api.otp.OtpResponseError
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.ResponseResendOtpFailed
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

interface OtpRepository {
    suspend fun validateOtp(userId: String, otp: String): Flow<ValidateOtpRepositoryResponse>
    suspend fun resendOtp(requestResendOtp: RequestResendOtp): Flow<ResendOtpRepositoryResponse>
}

@Single
class OtpRepositoryImpl(
    private val apiClient: ApiClient,
    private val gson: Gson
) : OtpRepository {

    override suspend fun validateOtp(
        userId: String,
        otp: String
    ): Flow<ValidateOtpRepositoryResponse> =
        flow {
            val otpRequest = OtpRequest(
                userId = userId,
                otp = otp
            )
            apiClient.validateOtp(otpRequest).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ValidateOtpRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ValidateOtpRepositoryResponse.Failed(
                            otpResponseError = gson.fromJson(
                                response.errorBody()?.string(),
                                OtpResponseError::class.java
                            )
                        )
                    )
                }
            }
        }

    override suspend fun resendOtp(requestResendOtp: RequestResendOtp): Flow<ResendOtpRepositoryResponse> =
        flow {
            apiClient.resendOtp(requestResendOtp).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ResendOtpRepositoryResponse.Success(
                            responseResendOtpSuccess = response.body()
                        )
                    )
                } else {
                    emit(
                        ResendOtpRepositoryResponse.Failed(
                            responseResendOtpFailed = gson.fromJson(
                                response.errorBody()?.string(),
                                ResponseResendOtpFailed::class.java
                            )
                        )
                    )
                }
            }
        }
}

sealed class ValidateOtpRepositoryResponse {
    data class Success(val response: ResponseToken?) : ValidateOtpRepositoryResponse()
    data class Failed(val otpResponseError: OtpResponseError) : ValidateOtpRepositoryResponse()
}

sealed class ResendOtpRepositoryResponse {
    data class Success(val responseResendOtpSuccess: ResponseResendOtpSuccess?) :
        ResendOtpRepositoryResponse()

    data class Failed(val responseResendOtpFailed: ResponseResendOtpFailed) :
        ResendOtpRepositoryResponse()
}
