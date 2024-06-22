package com.alexgreece45.marvelx.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.domain.model_api.userAuthData.UserAuthData
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import com.alexgreece45.marvelx.util.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefreshTokenWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val refreshTokenClient: RefreshTokenClient,
    private val authController: AuthController,
    private val configOauth: ConfigOauth,
    private val logger: AppLogger
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
                    refreshTokenClient.refreshAccessToken(configOauth.clientId, refreshToken)
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

                                    logger.d("RefreshTokenWorker") { "Access token was successfully refreshed." }
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
        logger.e("RefreshTokenWorker") { "(RefreshTokenWorker#doWork) error message: $errorMessage" }
        return Result.failure()
    }
}