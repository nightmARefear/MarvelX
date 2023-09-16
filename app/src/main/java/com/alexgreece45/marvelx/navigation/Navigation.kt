package com.alexgreece45.marvelx.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.getAfterSuccessfulLoginScreen().route
    ) {
        composable(screen = Screen.AllCharactersScreen) {
            AllCharactersScreen(
                navController = navController
            )
        }

        composable(
            screen = Screen.CharacterScreen
        ) { navBackStackEntry ->
            CharacterScreen(
                navController = navController,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.Character.key)
                    ?: -1
            )
        }

        composable(
            screen = Screen.AllCharacterComicsScreen
        ) { navBackStackEntry ->
            AllCharacterComicsScreen(
                navController = navController,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.Character.key) ?: -1,
                toolbarTitlePrefix = navBackStackEntry.arguments?.getString(NavItem.ToolbarTitlePrefix.key)
                    .orEmpty()
            )
        }

        composable(Screen.AllFavoriteCharactersScreen) {
            AllFavoriteCharactersScreen(navController = navController)
        }

        composable(Screen.ProfileScreen) {
            ProfileScreen(navController = navController)
        }
    }
}

private fun NavGraphBuilder.composable(
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