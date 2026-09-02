package com.alexgreece45.marvelx.ui.screens.comic.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.comic.ComicScreen
import com.alexgreece45.marvelx.ui.screens.comic.ComicViewModel
import com.alexgreece45.marvelx.ui.screens.comic.model.ComicContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class ComicRoute(val comicId: Int = -1)

fun NavController.navigateToComicScreen(comicId: Int) =
    navigate(
        route = ComicRoute(comicId),
        navOptions = navOptions {
            launchSingleTop = true
        }
    )

internal fun NavGraphBuilder.comicScreen(
    goBack: () -> Unit
) {
    composable<ComicRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<ComicRoute>()
        val viewModel = koinViewModel<ComicViewModel>()
        ComicScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    ComicContract.Effect.Navigation.GoBack -> goBack()
                }
            },
            comicId = route.comicId,
        )
    }
}