package com.alexgreece45.marvelx.application.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker

class RefreshTokenWorkerFactory(
    private val refreshTokenClient: RefreshTokenClient,
    private val userSessionRepository: UserSessionRepository,
    private val configOauthLogic: ConfigOauthLogic,
    private val logger: AppLogger
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RefreshTokenWorker(
        context = appContext,
        workerParameters = workerParameters,
        refreshTokenClient = refreshTokenClient,
        userSessionRepository = userSessionRepository,
        configOauthLogic = configOauthLogic,
        logger = logger
    )
}