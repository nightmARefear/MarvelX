package com.alexgreece45.marvelx.controllers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.extensions.LOGGER_TAG
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit

interface RefreshTokenController {

    /**
     * Starts the refresh token worker.
     */
    fun startWorker()

    /**
     * Stops the refresh token worker.
     */
    fun stopWorker()
}

@Single
class RefreshTokenControllerImpl(
    private val context: Context,
    private val configLogic: ConfigLogic,
    private val logger: AppLogger
) : RefreshTokenController {

    override fun startWorker() {
        try {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                ConstantsWorker.RefreshToken.WORKER_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequest
                    .Builder(
                        RefreshTokenWorker::class.java,
                        configLogic.refreshTokenInterval,
                        TimeUnit.MINUTES
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
            logger.i(LOGGER_TAG) { "Refresh token worker started." }
        } catch (e: Exception) {
            logger.e(LOGGER_TAG) {
                "Refresh token worker could NOT be started.\n" +
                        "(startWorker) error: $e"
            }
            throw e
        }
    }

    override fun stopWorker() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(ConstantsWorker.RefreshToken.WORKER_NAME)
        logger.i(LOGGER_TAG) { "Refresh token worker stopped." }
    }
}