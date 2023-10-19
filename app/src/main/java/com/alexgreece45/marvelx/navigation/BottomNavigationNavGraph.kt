package com.alexgreece45.marvelx.navigation

import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.domain.viewmodels.ActivityState
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.comicScreen.ComicScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen

fun NavGraphBuilder.BottomNavigationNavGraph(
    navController: NavController,
    activityState: State<ActivityState>
) =
    navigation(
        route = Screen.BottomNavigationScreens.getNestedGraphRootRoute(),
        startDestination = Screen.BottomNavigationScreens.getStartDestination()
    ) {
        composable(screen = Screen.BottomNavigationScreens.AllCharactersScreen) { navBackStackEntry ->
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
            ProfileScreen(
                navController = navController,
                activityState = activityState
            )
        }
    }