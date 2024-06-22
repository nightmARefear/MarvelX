package com.alexgreece45.marvelx

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.di.initKoin
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject

class MarvelXApplication : Application(), Configuration.Provider {

    private val appLogger: AppLogger by inject()
    private val refreshTokenWorkerFactory: RefreshTokenWorkerFactory by inject()
    private val configSecurityLogic: ConfigSecurityLogic by inject()

    override fun onCreate() {
        super.onCreate()

        // Initialize firebase.
        FirebaseApp.initializeApp(this)

        // Initialize dependencies.
        initKoin(this)

        // Install logger.
        appLogger.installLogger()

        // Handle strict mode.
        handleStrictMode()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(refreshTokenWorkerFactory)
            .build()

    /**
     * Handles strict mode if it has been enabled in the security config.
     */
    private fun handleStrictMode() {
        if (configSecurityLogic.enableStrictMode) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}

class RefreshTokenWorkerFactory(
    private val refreshTokenClient: RefreshTokenClient,
    private val authController: AuthController,
    private val configOauth: ConfigOauth,
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
        authController = authController,
        configOauth = configOauth,
        logger = logger
    )
}
