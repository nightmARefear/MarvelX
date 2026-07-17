package com.alexgreece45.marvelx.ui.screens.allCharacters.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersViewModel
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object AllCharactersRoute

fun NavController.navigateToAllCharactersScreen(
    clearBackStack: Boolean
) = this.let { navController ->
    navController.navigate(AllCharactersRoute) {
        if (clearBackStack) {
            popUpToTop(navController)
        }
    }
}

internal fun NavGraphBuilder.allCharactersScreen(
    navController: NavController,
    navigateToCharacterScreen: (characterId: Int) -> Unit,
    openDeepLinkAction: (deepLinkAction: DeepLinkAction) -> Unit
) {
    composable<AllCharactersRoute> {
        val viewModel = koinViewModel<AllCharactersViewModel>()
        AllCharactersScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            navigateToCharacterScreen = navigateToCharacterScreen,
            openDeepLinkAction = openDeepLinkAction,
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
        )
    }
}