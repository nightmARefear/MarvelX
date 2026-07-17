package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

internal interface NavBackStackLogger {

    /**
     * Prints the nav back stack every time a navigation occurs.
     */
    @Composable
    fun LoggerView(navController: NavController)
}