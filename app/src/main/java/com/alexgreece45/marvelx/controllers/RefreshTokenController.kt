package com.alexgreece45.marvelx.controllers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.alexgreece45.marvelx.worker.RefreshTokenWorker
import java.util.concurrent.TimeUnit

interface RefreshTokenController {
    fun startWorker()

    fun stopWorker()
}

class RefreshTokenControllerImpl(
    private val resourceProvider: ResourceProvider,
    private val configLogic: ConfigLogic,
    private val logger: AppLoggerController
) : RefreshTokenController {
    override fun startWorker() {
        try {
            WorkManager.getInstance(resourceProvider.getContext()).enqueueUniquePeriodicWork(
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
            logger.i { "Refresh token worker started." }
        } catch (error: Exception) {
            logger.e {
                "Refresh token worker could NOT be started.\n" +
                        "(startWorker) error: $error"
            }
            throw error
        }
    }

    override fun stopWorker() {
        WorkManager.getInstance(resourceProvider.getContext())
            .cancelUniqueWork(ConstantsWorker.RefreshToken.WORKER_NAME)
        logger.i { "Refresh token worker stopped." }
    }
}