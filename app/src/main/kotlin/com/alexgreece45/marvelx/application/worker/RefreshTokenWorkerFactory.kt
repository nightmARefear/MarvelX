package com.alexgreece45.marvelx.application.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.storage.TokenRefresher
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker

class RefreshTokenWorkerFactory(
    private val tokenRefresher: TokenRefresher,
    private val userSessionRepository: UserSessionRepository,
    private val logger: AppLogger
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RefreshTokenWorker(
        context = appContext,
        workerParameters = workerParameters,
        tokenRefresher = tokenRefresher,
        userSessionRepository = userSessionRepository,
        logger = logger
    )
}
