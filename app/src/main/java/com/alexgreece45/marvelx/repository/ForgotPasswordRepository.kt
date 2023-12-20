package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordSendOtpToEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordValidateOtpFromEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordSendOtpToEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordValidateOtpFromEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordSendOtpToPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordValidateOtpFromPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordSendOtpToPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordValidateOtpFromPhoneNumber
import com.alexgreece45.marvelx.data.models.otp.OtpResponseError
import com.alexgreece45.marvelx.util.ResourceProvider
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ForgotPasswordRepository {
    /**
     * Forgot password flow. This method is used to send an OTP to the given [email].
     */
    suspend fun sendOtpToEmail(email: String): Flow<ForgotPasswordRepositorySendOtpEmailResponse>

    /**
     * Forgot password flow. This method is used to send an OTP to the given [phoneNumber].
     */
    suspend fun sendOtpToPhoneNumber(phoneNumber: String): Flow<ForgotPasswordRepositorySendOtpPhoneNumberResponse>

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
     * @param requestId     The unique request ID initially used to send the OTP. It will be used
     * for the validation in the backend.
     * @param otp           The OTP that the user has entered. It will be used for validation in the
     * backend.
     */
    suspend fun validateOtpFromEmail(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromEmailResponse>

    /**
     * Sends the given OTP to the backend for validation.
     *
     * @param requestId     The unique request ID initially used to send the OTP. It will be used
     * for the validation in the backend.
     * @param otp           The OTP that the user has entered. It will be used for validation in the
     * backend.
     */
    suspend fun validateOtpFromPhoneNumber(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse>
}

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider
) : ForgotPasswordRepository {
    override suspend fun sendOtpToEmail(email: String): Flow<ForgotPasswordRepositorySendOtpEmailResponse> =
        flow {
            apiClient.forgotPasswordSendOtpToEmail(
                body = RequestForgotPasswordSendOtpToEmail(email = email)
            ).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ForgotPasswordRepositorySendOtpEmailResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ForgotPasswordRepositorySendOtpEmailResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun sendOtpToPhoneNumber(phoneNumber: String): Flow<ForgotPasswordRepositorySendOtpPhoneNumberResponse> =
        flow {
            apiClient.forgotPasswordSendOtpToPhoneNumber(
                body = RequestForgotPasswordSendOtpToPhoneNumber(phoneNumber = phoneNumber)
            ).let { response ->
                if (response.isSuccessful) {
                    emit(
                        ForgotPasswordRepositorySendOtpPhoneNumberResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        ForgotPasswordRepositorySendOtpPhoneNumberResponse.Failed(
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

    override suspend fun validateOtpFromEmail(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromEmailResponse> = flow {
        apiClient.forgotPasswordValidateOtpFromEmail(
            body = RequestForgotPasswordValidateOtpFromEmail(
                requestId = requestId,
                otp = otp
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

    override suspend fun validateOtpFromPhoneNumber(
        requestId: String,
        otp: String
    ): Flow<ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse> = flow {
        apiClient.forgotPasswordValidateOtpFromPhoneNumber(
            body = RequestForgotPasswordValidateOtpFromPhoneNumber(
                requestId = requestId,
                otp = otp
            )
        ).let { response ->
            if (response.isSuccessful) {
                emit(
                    ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse.Success(
                        response = response.body()
                    )
                )
            } else {
                emit(
                    ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse.Failed(
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

sealed interface ForgotPasswordRepositorySendOtpEmailResponse {
    data class Success(val response: ResponseForgotPasswordSendOtpToEmail?) :
        ForgotPasswordRepositorySendOtpEmailResponse

    data class Failed(val errorMessage: String) : ForgotPasswordRepositorySendOtpEmailResponse
}

sealed interface ForgotPasswordRepositorySendOtpPhoneNumberResponse {
    data class Success(val response: ResponseForgotPasswordSendOtpToPhoneNumber?) :
        ForgotPasswordRepositorySendOtpPhoneNumberResponse

    data class Failed(val errorMessage: String) : ForgotPasswordRepositorySendOtpPhoneNumberResponse
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
    data class Success(val response: ResponseForgotPasswordValidateOtpFromEmail?) :
        ForgotPasswordRepositoryValidateOtpFromEmailResponse

    data class Failed(val otpResponseError: OtpResponseError) :
        ForgotPasswordRepositoryValidateOtpFromEmailResponse
}

sealed interface ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse {
    data class Success(val response: ResponseForgotPasswordValidateOtpFromPhoneNumber?) :
        ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse

    data class Failed(val otpResponseError: OtpResponseError) :
        ForgotPasswordRepositoryValidateOtpFromPhoneNumberResponse
}