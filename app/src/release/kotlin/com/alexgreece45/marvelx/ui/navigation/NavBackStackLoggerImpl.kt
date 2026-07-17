package com.alexgreece45.marvelx.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.annotation.Single

@Single
internal class NavBackStackLoggerImpl : NavBackStackLogger {

    @SuppressLint("RestrictedApi")
    @Composable
    override fun LoggerView(navController: NavController) {}
}