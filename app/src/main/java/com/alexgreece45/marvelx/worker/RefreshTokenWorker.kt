package com.alexgreece45.marvelx.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.ConfigController
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.userAuthData.UserAuthData
import com.alexgreece45.marvelx.util.DateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import logcat.LogPriority
import logcat.logcat

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val apiClient: ApiClient,
    private val authController: AuthController,
    private val configController: ConfigController
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        try {
            authController.getRefreshToken()?.let { refreshToken ->
                val accessTokenExpirationUnix = authController.getAccessTokenExpiration()
                val currentUnix = DateUtil.getSecondsFromEpoch()

                // Check if the access token does NOT expire in the next 10 to 20 seconds.
                if ((accessTokenExpirationUnix - currentUnix) !in 10..20) {
                    return failure("Too soon to refresh token.")
                }

                var result: Result

                // Access token will expire within the next 10 to 20 seconds. Thus, refresh it.
                apiClient.refreshAccessToken(configController.clientId, refreshToken).let { response ->
                    result = if (response.isSuccessful) {
                        response.body()?.let { tokenResponse ->
                            // Successfully retrieved new token.

                            // Store token.
                            authController.setData(
                                UserAuthData(
                                    accessToken = tokenResponse.accessToken,
                                    accessExpiresIn = tokenResponse.accessExpiresIn.toLong(),
                                    refreshToken = tokenResponse.refreshToken,
                                    refreshExpiresIn = tokenResponse.refreshExpiresIn.toLong(),
                                    tokenType = tokenResponse.tokenType
                                )
                            )
                            Result.success()
                        } ?: failure("Refresh access token body is null.")
                    } else {
                        failure("Refresh access token request failed.")
                    }
                }

                // Return result.
                return result
            } ?: return failure("Refresh token is null.")
        } catch (e: Exception) {
            return failure(e.message.orEmpty())
        }
    }

    private fun failure(errorMessage: String): Result {
        logcat(LogPriority.ERROR) { "(RefreshTokenWorker#doWork) error message: $errorMessage" }
        return Result.failure()
    }
}