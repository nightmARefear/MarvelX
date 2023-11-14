package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseError
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface OtpRepository {
    suspend fun validateOtp(otpRequest: OtpRequest): Flow<ValidateOtpRepositoryResponse>
}

class OtpRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val gson: Gson
) : OtpRepository {
    override suspend fun validateOtp(otpRequest: OtpRequest): Flow<ValidateOtpRepositoryResponse> =
        flow {
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
}

sealed class ValidateOtpRepositoryResponse {
    data class Success(val response: OtpResponseSuccess?) : ValidateOtpRepositoryResponse()
    data class Failed(val otpResponseError: OtpResponseError) : ValidateOtpRepositoryResponse()
    data class Error(val errorMessage: String?) : ValidateOtpRepositoryResponse()
}