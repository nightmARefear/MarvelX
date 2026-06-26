package com.alexgreece45.marvelx.application

import android.app.Application
import android.os.StrictMode
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.alexgreece45.marvelx.application.worker.RefreshTokenWorkerFactory
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.dispatchers.model.DispatcherQualifiers
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.di.initKoin
import com.alexgreece45.marvelx.ui.image.AuthenticatedImageLoader
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import kotlin.coroutines.CoroutineContext

class MarvelXApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    private val appLogger: AppLogger by inject()
    private val refreshTokenWorkerFactory: RefreshTokenWorkerFactory by inject()
    private val securityConfigLogic: SecurityConfigLogic by inject()
    private val authenticatedImageLoader: AuthenticatedImageLoader by inject()
    private val ioDispatcher: CoroutineContext by inject(named(DispatcherQualifiers.IO.qualifier))

    override fun onCreate() {
        super.onCreate()

        // Initialize firebase.
        FirebaseApp.initializeApp(this)

        // Initialize dependencies.
        initKoin(this@MarvelXApplication)

        // Install logger.
        appLogger.installLogger()

        // Handle strict mode.
        handleStrictMode()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(refreshTokenWorkerFactory)
            .setWorkerCoroutineContext(ioDispatcher)
            .build()

    override fun newImageLoader(): ImageLoader {
        return authenticatedImageLoader.newImageLoader()
    }

    /**
     * Handles strict mode if it has been enabled in the security config.
     */
    private fun handleStrictMode() {
        if (securityConfigLogic.enableStrictMode) {
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