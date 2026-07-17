package com.alexgreece45.marvelx.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.annotation.Single

@Single
internal class NavBackStackLoggerImpl(private val appLogger: AppLogger) : NavBackStackLogger {

    private val loggerTag = "NavBackStackLogger"

    @SuppressLint("RestrictedApi")
    @Composable
    override fun LoggerView(navController: NavController) {
        LaunchedEffect(navController) {
            navController.currentBackStack.collect { backStack ->
                appLogger.d(loggerTag) { "\n" }
                appLogger.d(loggerTag) { "================ Navigation Back Stack ================" }

                backStack.forEachIndexed { index, entry ->
                    val fullRoute = entry.destination.route ?: "No Route (Root)"
                    val cleanRoute = fullRoute.substringAfterLast(".")

                    appLogger.d(loggerTag) { "[$index] $cleanRoute  (ID: ${entry.id})" }
                }

                appLogger.d(loggerTag) { "=======================================================" }
            }
        }
    }
}