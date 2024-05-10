package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigation(
    navController: NavController
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        RegisterNavGraph(navController = navController)

        // Main screens.
        MainNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.composable(
    screen: Screen,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = screen.route,
        arguments = screen.navigationArguments
    ) {
        content(it)
    }
}