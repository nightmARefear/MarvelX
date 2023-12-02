package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.viewModels.ActivityState
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.comicScreen.ComicScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen

@Suppress("FunctionName")
fun NavGraphBuilder.MainNavGraph(
    navController: NavController,
    activityState: State<ActivityState>
) =
    navigation(
        route = Screen.BottomNavigationScreens.getNestedGraphRootRoute(),
        startDestination = Screen.BottomNavigationScreens.getStartDestination()
    ) {
        composable(screen = Screen.BottomNavigationScreens.AllCharactersScreen) {
            AllCharactersScreen(
                navigateToCharacterScreen = { characterId ->
                    navController.navigate(
                        Screen.CharacterScreen.createNavRoute(
                            characterId = characterId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                bottomNavigationBarData = { items, backgroundColor, topHorizontalColor, selectedItemTopHorizontalColor, elevation ->
                    BottomNavigationBarData(
                        navController = navController,
                        items = items,
                        backgroundColor = backgroundColor,
                        topHorizontalColor = topHorizontalColor,
                        selectedItemTopHorizontalColor = selectedItemTopHorizontalColor,
                        elevation = elevation
                    )
                }
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
            AllFavoriteCharactersScreen(
                bottomNavigationBarData = { items, backgroundColor, topHorizontalColor, selectedItemTopHorizontalColor, elevation ->
                    BottomNavigationBarData(
                        navController = navController,
                        items = items,
                        backgroundColor = backgroundColor,
                        topHorizontalColor = topHorizontalColor,
                        selectedItemTopHorizontalColor = selectedItemTopHorizontalColor,
                        elevation = elevation
                    )
                }
            )
        }

        composable(Screen.BottomNavigationScreens.ProfileScreen) {
            ProfileScreen(
                navigateToHomeScreen = {
                    navController.navigate(Screen.RegisterScreens.HomeScreen.route) {
                        popUpTo(Screen.BottomNavigationScreens.getNestedGraphRootRoute()) {
                            inclusive = true
                        }
                    }
                },
                bottomNavigationBarData = { items, backgroundColor, topHorizontalColor, selectedItemTopHorizontalColor, elevation ->
                    BottomNavigationBarData(
                        navController = navController,
                        items = items,
                        backgroundColor = backgroundColor,
                        topHorizontalColor = topHorizontalColor,
                        selectedItemTopHorizontalColor = selectedItemTopHorizontalColor,
                        elevation = elevation
                    )
                }
            )
        }
    }