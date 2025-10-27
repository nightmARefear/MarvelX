package com.alexgreece45.marvelx.application

import android.app.Application
import android.os.StrictMode
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.alexgreece45.marvelx.application.worker.RefreshTokenWorkerFactory
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.di.initKoin
import com.alexgreece45.marvelx.data.repository.imageLoader.AuthenticatedImageLoader
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject

class MarvelXApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    private val appLogger: AppLogger by inject()
    private val refreshTokenWorkerFactory: RefreshTokenWorkerFactory by inject()
    private val configSecurityLogic: ConfigSecurityLogic by inject()
    private val authenticatedImageLoader: AuthenticatedImageLoader by inject()

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

    override fun newImageLoader(): ImageLoader {
        return authenticatedImageLoader.newImageLoader()
    }

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
