package com.alexgreece45.marvelx.controllers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.alexgreece45.marvelx.util.REFRESH_TOKEN_WORKER_NAME
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.worker.RefreshTokenWorker
import logcat.LogPriority
import logcat.logcat
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RefreshTokenController {
    fun startWorker()

    fun stopWorker()
}

class RefreshTokenControllerImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val configController: ConfigController
) : RefreshTokenController {
    override fun startWorker() {
        try {
            WorkManager.getInstance(resourceProvider.getContext()).enqueueUniquePeriodicWork(
                REFRESH_TOKEN_WORKER_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequest
                    .Builder(
                        RefreshTokenWorker::class.java,
                        configController.refreshTokenInterval,
                        TimeUnit.MINUTES
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
            logcat(LogPriority.INFO) { "Refresh token worker started." }
        } catch (error: Exception) {
            logcat(LogPriority.ERROR) { "Refresh token worker could NOT be started.\n" +
                    "(startWorker) error: $error" }
            throw error
        }
    }

    override fun stopWorker() {
        WorkManager.getInstance(resourceProvider.getContext())
            .cancelUniqueWork(REFRESH_TOKEN_WORKER_NAME)
        logcat(LogPriority.INFO) { "Refresh token worker stopped." }
    }
}