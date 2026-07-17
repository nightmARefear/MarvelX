package com.alexgreece45.marvelx.ui.navigation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.AllCharactersRoute
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.allCharactersScreen
import com.alexgreece45.marvelx.ui.screens.character.navigation.navigateToCharacterScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.marketplaceGraph
import kotlinx.serialization.Serializable

@Serializable
private data object MainGraphRoute

internal fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    navigation<MainGraphRoute>(startDestination = AllCharactersRoute) {
        marketplaceGraph(
            navController = navController,
            bottomNavigationBarData = bottomNavigationBarData
        )

        allCharactersScreen(
            navController = navController,
            navigateToCharacterScreen = navController::navigateToCharacterScreen,
            openDeepLinkAction = navController::openDeepLinkAction
        )
    }
}