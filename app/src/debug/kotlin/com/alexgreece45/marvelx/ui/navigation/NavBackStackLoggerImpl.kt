package com.alexgreece45.marvelx.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import kotlinx.coroutines.flow.collectIndexed
import org.koin.core.annotation.Single

@Single
internal class NavBackStackLoggerImpl(
    private val appLogger: AppLogger
) : NavBackStackLogger {

    private val loggerTag = "NavBackStackLogger"

    @SuppressLint("RestrictedApi")
    @Composable
    override fun TrackBackStack(navController: NavController, showRouteId: Boolean) {
        LaunchedEffect(navController) {
            navController.currentBackStack.collectIndexed { index, backStack ->
                appLogger.d(loggerTag) { "\n" }
                val startLog = "================ Navigation Back Stack [#$index] ================"
                appLogger.d(loggerTag) { startLog }

                backStack.forEachIndexed { stackIndex, entry ->
                    val fullRoute = entry.destination.route ?: "No Route (Root)"
                    val cleanRoute = fullRoute
                        .substringAfterLast(".")
                        .substringBefore("?") // For older nav arg system.
                        .substringBefore("/") // For new nav arg system.

                    val prefix = "[$stackIndex] "

                    val args = entry.arguments
                    val userKeys = args?.keySet()?.filter { !it.startsWith("android-support-nav") }
                        ?: emptyList()

                    if (userKeys.isNotEmpty()) {
                        val formattedArgs = userKeys.joinToString(", ") { key ->
                            "$key=${args!!.getString(key)}"
                        }

                        // Create blank space padding exactly the length of the prefix.
                        val indent = " ".repeat(prefix.length)

                        appLogger.d(loggerTag) {
                            "$prefix$cleanRoute  (ID: ${entry.id})\n${indent}Args: {$formattedArgs}"
                        }
                    } else {
                        appLogger.d(loggerTag) { "$prefix$cleanRoute" + if (showRouteId) "(ID: ${entry.id})" else "" }
                    }
                }

                appLogger.d(loggerTag) { "=".repeat(startLog.length) }
            }
        }
    }
}