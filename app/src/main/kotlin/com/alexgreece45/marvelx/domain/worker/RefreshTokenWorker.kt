package com.alexgreece45.marvelx.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.alexgreece45.marvelx.util.DateUtil

class RefreshTokenWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val refreshTokenClient: RefreshTokenClient,
    private val userSessionRepository: UserSessionRepository,
    private val configOauthLogic: ConfigOauthLogic,
    private val logger: AppLogger
) : CoroutineWorker(context, workerParameters) {

    private val loggerTag = "RefreshTokenWorker"

    override suspend fun doWork(): Result {
        return try {
            val refreshToken =
                userSessionRepository.getRefreshToken() ?: return failure("Refresh token is null.")

            val accessTokenExpirationUnix = userSessionRepository.getAccessTokenExpiration()
            val currentUnix = DateUtil.getEpochSeconds()
            val totalSecondsRemainingFromExpiration = accessTokenExpirationUnix - currentUnix
            val minutesRemainingFromExpiration = totalSecondsRemainingFromExpiration / 60
            val secondsRemainingFromExpiration =
                totalSecondsRemainingFromExpiration - minutesRemainingFromExpiration * 60

            // Check if the access token expires in the next 20 seconds.
            if (totalSecondsRemainingFromExpiration <= 20) {
                var result: Result

                // Access token will expire within the next 20 seconds. Thus, refresh it.
                val accessTokenResponse =
                    refreshTokenClient.refreshAccessToken(configOauthLogic.clientId, refreshToken)

                result = if (accessTokenResponse.isSuccessful) {
                    val body = accessTokenResponse.body()
                    if (body != null) {
                        // Store token.
                        userSessionRepository.setAuthenticationTokens(
                            AuthenticationTokens(
                                accessToken = body.accessToken,
                                accessExpiresIn = body.accessExpiresIn.toLong(),
                                refreshToken = body.refreshToken,
                                refreshExpiresIn = body.refreshExpiresIn.toLong(),
                                tokenType = body.tokenType
                            )
                        )

                        logger.d(loggerTag) { "Access token was successfully refreshed." }
                        Result.success()
                    } else {
                        failure("Refresh access token body is null.")

                    }
                } else {
                    failure("Refresh access token request failed.")
                }

                // Return result.
                result
            } else {
                failure("Too soon to refresh token ($minutesRemainingFromExpiration'$secondsRemainingFromExpiration\" remaining).")
            }
        } catch (e: Exception) {
            failure(e.message.orEmpty())
        }
    }

    private fun failure(errorMessage: String): Result {
        logger.e(loggerTag) { "(doWork) error message: $errorMessage" }
        return Result.failure()
    }
}