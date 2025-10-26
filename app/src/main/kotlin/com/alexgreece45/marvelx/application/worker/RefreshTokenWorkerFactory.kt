package com.alexgreece45.marvelx.application.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import kotlin.coroutines.CoroutineContext

class RefreshTokenWorkerFactory(
    private val refreshTokenClient: RefreshTokenClient,
    private val authController: AuthController,
    private val configOauth: ConfigOauth,
    private val logger: AppLogger,
    private val ioDispatcher: CoroutineContext,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RefreshTokenWorker(
        context = appContext,
        workerParameters = workerParameters,
        refreshTokenClient = refreshTokenClient,
        authController = authController,
        configOauth = configOauth,
        logger = logger,
        ioDispatcher = ioDispatcher,
    )
}
