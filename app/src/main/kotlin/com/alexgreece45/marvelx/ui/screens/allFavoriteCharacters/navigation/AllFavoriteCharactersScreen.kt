package com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object AllFavoriteCharactersRoute

fun NavController.navigateToAllFavoriteCharactersScreen() = navigate(AllFavoriteCharactersRoute)

internal fun NavGraphBuilder.allFavoriteCharactersScreen(
    navController: NavController
) {
    composable<AllFavoriteCharactersRoute> {
        val viewModel = koinViewModel<AllFavoriteCharactersViewModel>()
        AllFavoriteCharactersScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
        )
    }
}