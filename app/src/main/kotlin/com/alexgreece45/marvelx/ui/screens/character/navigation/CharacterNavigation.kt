package com.alexgreece45.marvelx.ui.screens.character.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.character.CharacterEffect
import com.alexgreece45.marvelx.ui.screens.character.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.character.CharacterViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class CharacterRoute(val characterId: Int = -1)

fun NavController.navigateToCharacterScreen(
    characterId: Int,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
) = navigate(
    route = CharacterRoute(characterId),
    navOptions = navOptionsBuilder?.let { navOptions(it) })

fun NavGraphBuilder.characterScreen(
    goBack: () -> Unit,
    navigateToComicScreen: (comicId: Int) -> Unit,
    navigateToAllCharacterComicsScreen: (characterId: Int, toolbarTitlePrefix: String) -> Unit
) {
    composable<CharacterRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<CharacterRoute>()
        val viewModel = koinViewModel<CharacterViewModel>()

        CharacterScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            characterId = route.characterId,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    is CharacterEffect.Navigation.NavigateToAllCharacterComicsScreen -> navigateToAllCharacterComicsScreen(
                        navigationEffect.characterId,
                        navigationEffect.toolbarTitlePrefix
                    )

                    is CharacterEffect.Navigation.NavigateToComicScreen -> navigateToComicScreen(
                        navigationEffect.comicId
                    )

                    CharacterEffect.Navigation.GoBack -> goBack()
                }
            }
        )
    }
}