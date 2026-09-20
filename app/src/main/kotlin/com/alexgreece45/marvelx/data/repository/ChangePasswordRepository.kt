package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.RequestForgotPasswordResetPassword
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface ChangePasswordRepository {

    /**
     * Sets a new password for the forgot-password request identified by [requestId]. The request's
     * OTP must already have been validated server-side.
     *
     * @param hashedPassword The password hashed client-side, exactly as signup/login send it.
     */
    suspend fun resetPassword(
        requestId: String,
        verificationStep: String,
        hashedPassword: String
    ): RepositoryResult<String>
}

@Single
internal class ChangePasswordRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : ChangePasswordRepository {

    override suspend fun resetPassword(
        requestId: String,
        verificationStep: String,
        hashedPassword: String
    ): RepositoryResult<String> =
        handleApiResultNoTransformationNoFlow(
            apiCall = {
                apiClient.forgotPasswordResetPassword(
                    RequestForgotPasswordResetPassword(
                        requestId = requestId,
                        verificationStep = verificationStep,
                        newPassword = hashedPassword
                    )
                )
            },
            ioDispatcher = ioDispatcher
        )
}
