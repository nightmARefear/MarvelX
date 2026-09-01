package com.alexgreece45.marvelx.ui.screens.dashboard.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.screens.dashboard.DashboardScreen
import com.alexgreece45.marvelx.ui.screens.dashboard.DashboardViewModel
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data object DashboardRoute

internal fun NavController.navigateToDashboardScreen() = this.let { navController ->
    navController.navigate(DashboardRoute) {
        popUpToTop(navController)
    }
}

internal fun NavGraphBuilder.dashboardGraph(navController: NavHostController) =
    composable<DashboardRoute> {
        val viewModel = koinViewModel<DashboardViewModel>()
        DashboardScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            rootNavController = navController
        )
    }