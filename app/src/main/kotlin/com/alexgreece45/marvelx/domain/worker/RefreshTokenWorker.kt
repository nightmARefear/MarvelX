package com.alexgreece45.marvelx.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.storage.TokenRefreshResult
import com.alexgreece45.marvelx.core.network.storage.TokenRefresher
import com.alexgreece45.marvelx.util.DateUtil

class RefreshTokenWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val tokenRefresher: TokenRefresher,
    private val userSessionRepository: UserSessionRepository,
    private val logger: AppLogger
) : CoroutineWorker(context, workerParameters) {

    private val loggerTag = "RefreshTokenWorker"

    override suspend fun doWork(): Result {
        return try {
            if (userSessionRepository.getRefreshToken() == null) {
                return failure("Refresh token is null.")
            }

            val accessTokenExpirationUnix = userSessionRepository.getAccessTokenExpiration()
            val currentUnix = DateUtil.getEpochSeconds()
            val totalSecondsRemainingFromExpiration = accessTokenExpirationUnix - currentUnix
            val minutesRemainingFromExpiration = totalSecondsRemainingFromExpiration / 60
            val secondsRemainingFromExpiration =
                totalSecondsRemainingFromExpiration - minutesRemainingFromExpiration * 60

            // Check if the access token expires in the next 20 seconds.
            if (totalSecondsRemainingFromExpiration <= 20) {
                // Access token will expire within the next 20 seconds. Thus, refresh it (the
                // refresher also stores the new tokens).
                when (val result = tokenRefresher.refresh()) {
                    is TokenRefreshResult.Success -> {
                        logger.d(loggerTag) { "Access token was successfully refreshed." }
                        Result.success()
                    }

                    is TokenRefreshResult.Failed -> failure(result.reason)
                    is TokenRefreshResult.Error -> failure(result.throwable.message.orEmpty())
                }
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
