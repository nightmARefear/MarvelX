package com.alexgreece45.marvelx

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.worker.RefreshTokenWorker
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import javax.inject.Inject

@HiltAndroidApp
class MarvelXApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var refreshTokenWorkerFactory: RefreshTokenWorkerFactory

    @Inject
    lateinit var configLogic: ConfigLogic

    override fun onCreate() {
        super.onCreate()

        // Install logcat if it hasn't already been installed, and if the build type isn't release.
        if (LogcatLogger.isInstalled.not() && configLogic.appBuildType != AppBuildType.RELEASE) {
            LogcatLogger.install(AndroidLogcatLogger(LogPriority.DEBUG))
        }
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(refreshTokenWorkerFactory)
            .build()
}

class RefreshTokenWorkerFactory @Inject constructor(
    private val apiClient: ApiClient,
    private val authController: AuthController,
    private val configLogic: ConfigLogic
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RefreshTokenWorker(
        context = appContext,
        workerParameters = workerParameters,
        apiClient = apiClient,
        authController = authController,
        configLogic = configLogic
    )
}