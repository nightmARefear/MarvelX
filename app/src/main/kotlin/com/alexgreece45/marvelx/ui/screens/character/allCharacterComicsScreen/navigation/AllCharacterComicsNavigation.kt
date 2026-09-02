package com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsEffect
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class AllCharacterComicsRoute(
    val characterId: Int = -1,
    val toolbarTitlePrefix: String = ""
)

fun NavController.navigateToAllCharacterComicsScreen(characterId: Int, toolbarTitlePrefix: String) =
    navigate(AllCharacterComicsRoute(characterId, toolbarTitlePrefix))

internal fun NavGraphBuilder.allCharacterComicsScreen(
    goBack: () -> Unit,
    navigateToComicScreen: (comicId: Int) -> Unit,
) {
    composable<AllCharacterComicsRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<AllCharacterComicsRoute>()
        val viewModel = koinViewModel<AllCharacterComicsViewModel>()

        AllCharacterComicsScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    AllCharacterComicsEffect.Navigation.GoBack -> goBack()
                    is AllCharacterComicsEffect.Navigation.NavigateToComicScreen -> navigateToComicScreen(
                        navigationEffect.comicId
                    )
                }
            },
            characterId = route.characterId,
            toolbarTitlePrefix = route.toolbarTitlePrefix
        )
    }
}