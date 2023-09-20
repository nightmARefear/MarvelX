package com.alexgreece45.marvelx.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.comicScreen.ComicScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen.HomeScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen.LogInScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.signUpScreen.SignUpScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        navigation(
            route = Screen.RegisterScreens.getNestedGraphRootRoute(),
            startDestination = Screen.RegisterScreens.getStartDestination()
        ) {
            composable(Screen.RegisterScreens.HomeScreen) {
                HomeScreen(navController = navController)
            }

            composable(Screen.RegisterScreens.LogInScreen) {
                LogInScreen(navController = navController)
            }

            composable(Screen.RegisterScreens.SignUpScreen) {
                SignUpScreen(navController = navController)
            }
        }

        // Bottom Navigation screens.
        navigation(
            route = Screen.BottomNavigationScreens.getNestedGraphRootRoute(),
            startDestination = Screen.BottomNavigationScreens.getStartDestination()
        ) {
            composable(screen = Screen.BottomNavigationScreens.AllCharactersScreen) {
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
                screen = Screen.ComicScreen
            ) { navBackStackEntry ->
                ComicScreen(
                    navController = navController,
                    comicId = navBackStackEntry.arguments?.getInt(NavItem.Comic.key) ?: -1
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

            composable(Screen.BottomNavigationScreens.AllFavoriteCharactersScreen) {
                AllFavoriteCharactersScreen(navController = navController)
            }

            composable(Screen.BottomNavigationScreens.ProfileScreen) {
                ProfileScreen(navController = navController)
            }
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