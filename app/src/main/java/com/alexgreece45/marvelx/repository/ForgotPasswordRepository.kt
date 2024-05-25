package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordSendOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordValidateOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordSendOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordValidateOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models_api.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models_api.otp.OtpResponseError
import com.alexgreece45.marvelx.util.ResourceProvider
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ForgotPasswordRepository {
    /**
     * Forgot password flow. This method is used to send an OTP to the given [data] based on the
     * [verificationStep].
     */
    suspend fun sendOtp(
        data: String,
        verificationStep: String
    ): Flow<ForgotPasswordRepositorySendOtpResponse>

    /**
     * Checks if the OTP functionality for the email is blocked.
     */
    suspend fun isOtpBlockedEmail(requestId: String): Flow<ForgotPasswordRepositoryIsOtpBlockedEmailResponse>

    /**
     * Checks if the OTP functionality for the phone number is blocked.
     */
    suspend fun isOtpBlockedPhoneNumber(requestId: String): Flow<ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse>

    /**
     * Sends the given OTP to the backend for validation.
     *
     * @param requestId         The unique request ID initially used to send the OTP. It will be
     * used for the validation in the backend.
     * @param otp               The OTP that the user has entered. It will be used for validation in
     * the backend.
     * @param verificationStep  The verification step as a [String] describing if the step is
     * validation via email or phone number.
     */
    suspend fun validateOtp(
        requestId: String,
        otp: String,
        verificationStep: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromEmailResponse>
}

class ForgotPasswordRepositoryImpl(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider
) : ForgotPasswordRepository {
    override suspend fun sendOtp(
        data: String,
        verificationStep: String
    ): Flow<ForgotPasswordRepositorySendOtpResponse> =
        flow {
            apiClient.forgotPasswordSendOtp(
                body = RequestForgotPasswordSendOtp(
                    data = data,
                    verificationStep = verificationStep
                )
            ).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ForgotPasswordRepositorySendOtpResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ForgotPasswordRepositorySendOtpResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun isOtpBlockedEmail(requestId: String): Flow<ForgotPasswordRepositoryIsOtpBlockedEmailResponse> =
        flow {
            apiClient.forgotPasswordIsOtpBlockedEmail(
                body = RequestForgotPasswordOtpBlockedEmail(requestId = requestId)
            ).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ForgotPasswordRepositoryIsOtpBlockedEmailResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ForgotPasswordRepositoryIsOtpBlockedEmailResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun isOtpBlockedPhoneNumber(requestId: String): Flow<ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse> =
        flow {
            apiClient.forgotPasswordIsOtpBlockedPhoneNumber(
                body = RequestForgotPasswordOtpBlockedPhoneNumber(requestId = requestId)
            ).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun validateOtp(
        requestId: String,
        otp: String,
        verificationStep: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromEmailResponse> = flow {
        apiClient.forgotPasswordValidateOtp(
            body = RequestForgotPasswordValidateOtp(
                requestId = requestId,
                otp = otp,
                verificationStep = verificationStep
            )
        ).let { response ->
            if (response.isSuccessful) {
                emit(
                    ForgotPasswordRepositoryValidateOtpFromEmailResponse.Success(
                        response = response.body()
                    )
                )
            } else {
                emit(
                    ForgotPasswordRepositoryValidateOtpFromEmailResponse.Failed(
                        otpResponseError = Gson().fromJson(
                            response.errorBody()?.string(),
                            OtpResponseError::class.java
                        )
                    )
                )
            }
        }
    }
}

sealed interface ForgotPasswordRepositorySendOtpResponse {
    data class Success(val response: ResponseForgotPasswordSendOtp?) :
        ForgotPasswordRepositorySendOtpResponse

    data class Failed(val errorMessage: String) : ForgotPasswordRepositorySendOtpResponse
}

sealed interface ForgotPasswordRepositoryIsOtpBlockedEmailResponse {
    data class Success(val response: ResponseForgotPasswordOtpBlockedEmail?) :
        ForgotPasswordRepositoryIsOtpBlockedEmailResponse

    data class Failed(val errorMessage: String) : ForgotPasswordRepositoryIsOtpBlockedEmailResponse
}

sealed interface ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse {
    data class Success(val response: ResponseForgotPasswordOtpBlockedPhoneNumber?) :
        ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse

    data class Failed(val errorMessage: String) :
        ForgotPasswordRepositoryIsOtpBlockedPhoneNumberResponse
}

sealed interface ForgotPasswordRepositoryValidateOtpFromEmailResponse {
    data class Success(val response: ResponseForgotPasswordValidateOtp?) :
        ForgotPasswordRepositoryValidateOtpFromEmailResponse

    data class Failed(val otpResponseError: OtpResponseError) :
        ForgotPasswordRepositoryValidateOtpFromEmailResponse
}