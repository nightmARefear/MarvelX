package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.comicScreen.ComicScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen

@Suppress("FunctionName")
fun NavGraphBuilder.MainNavGraph(
    navController: NavController
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
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }

        composable(
            screen = Screen.CharacterScreen
        ) { navBackStackEntry ->
            CharacterScreen(
                navController = navController,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key)
                    ?: -1
            )
        }

        composable(
            screen = Screen.ComicScreen
        ) { navBackStackEntry ->
            ComicScreen(
                navController = navController,
                comicId = navBackStackEntry.arguments?.getInt(NavItem.ComicId.key) ?: -1
            )
        }

        composable(
            screen = Screen.AllCharacterComicsScreen
        ) { navBackStackEntry ->
            AllCharacterComicsScreen(
                navController = navController,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key) ?: -1,
                toolbarTitlePrefix = navBackStackEntry.arguments?.getString(NavItem.ToolbarTitlePrefix.key)
                    .orEmpty()
            )
        }

        composable(Screen.BottomNavigationScreens.AllFavoriteCharactersScreen) {
            AllFavoriteCharactersScreen(
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
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
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }
    }

/**
 * Returns the bottom navigation items.
 */
private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
    BottomBarItem(
        route = Screen.BottomNavigationScreens.AllCharactersScreen.route,
        label = R.string.bottom_navigation_label_characters,
        iconData = IconData(
            iconResource = R.drawable.ic_superhero,
            contentDescription = R.string.bottom_navigation_label_characters
        )
    ),
    BottomBarItem(
        route = Screen.BottomNavigationScreens.AllFavoriteCharactersScreen.route,
        label = R.string.bottom_navigation_label_favorites,
        iconData = IconData(
            iconResource = R.drawable.ic_favorite_filled,
            contentDescription = R.string.bottom_navigation_label_favorites
        )
    ),
    BottomBarItem(
        route = Screen.BottomNavigationScreens.ProfileScreen.route,
        label = R.string.bottom_navigation_label_profile,
        iconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.bottom_navigation_label_profile
        )
    )
)

/**
 * Returns the [BottomNavigationBarData] required for the bottom navigation bar in the main screens.
 *
 * @param navController The associated [NavController] of the graph.
 */
@Composable
private fun getBottomNavigationBarData(navController: NavController): BottomNavigationBarData =
    BottomNavigationBarData(
        navController = navController,
        items = getBottomNavigationItems(),
        backgroundColor = MaterialTheme.colors.background,
        topHorizontalColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
        selectedItemTopHorizontalColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.8f),
        elevation = 16.dp
    )