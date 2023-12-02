package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.viewModels.ActivityState

@Composable
fun Navigation(
    activityState: State<ActivityState>
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        RegisterNavGraph(navController = navController)

        // Main screens.
        MainNavGraph(navController = navController, activityState = activityState)
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