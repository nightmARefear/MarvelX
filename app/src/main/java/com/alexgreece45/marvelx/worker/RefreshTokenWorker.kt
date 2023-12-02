package com.alexgreece45.marvelx.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.userAuthData.UserAuthData
import com.alexgreece45.marvelx.util.DateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    @Assisted private val apiClient: ApiClient,
    @Assisted private val authController: AuthController,
    @Assisted private val configLogic: ConfigLogic,
    @Assisted private val logger: AppLoggerController
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            authController.getRefreshToken()?.let { refreshToken ->
                val accessTokenExpirationUnix = authController.getAccessTokenExpiration()
                val currentUnix = DateUtil.getSecondsFromEpoch()
                val totalSecondsRemainingFromExpiration = accessTokenExpirationUnix - currentUnix
                val minutesRemainingFromExpiration = totalSecondsRemainingFromExpiration / 60
                val secondsRemainingFromExpiration =
                    totalSecondsRemainingFromExpiration - minutesRemainingFromExpiration * 60

                // Check if the access token expires in the next 20 seconds.
                if (totalSecondsRemainingFromExpiration <= 20) {
                    var result: Result

                    // Access token will expire within the next 20 seconds. Thus, refresh it.
                    apiClient.refreshAccessToken(configLogic.clientId, refreshToken)
                        .let { response ->
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

                                    logger.i { "Access token was successfully refreshed." }
                                    Result.success()
                                } ?: failure("Refresh access token body is null.")
                            } else {
                                failure("Refresh access token request failed.")
                            }
                        }

                    // Return result.
                    result
                } else {
                    failure("Too soon to refresh token ($minutesRemainingFromExpiration'$secondsRemainingFromExpiration\" remaining).")
                }
            } ?: failure("Refresh token is null.")
        } catch (e: Exception) {
            failure(e.message.orEmpty())
        }
    }

    private fun failure(errorMessage: String): Result {
        logger.e { "(RefreshTokenWorker#doWork) error message: $errorMessage" }
        return Result.failure()
    }
}