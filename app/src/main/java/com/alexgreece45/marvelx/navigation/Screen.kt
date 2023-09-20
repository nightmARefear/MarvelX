package com.alexgreece45.marvelx.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    protected val baseRoute: String,
    private val navArgs: List<NavItem> = emptyList()
) {
    /**
     * The route of the screen with the navigation arguments included.
     * */
    val route: String
        get() = listOf(baseRoute)
            .plus(navArgs.map { "{${it.key}}" })
            .joinToString("/")

    /**
     * Converts a list of [NavItem]s to a list of [NamedNavArgument]s to be used by NavGraphBuilder#composable.
     */
    val navigationArguments = navArgs.map { navItem ->
        navArgument(name = navItem.key) {
            type = navItem.type
        }
    }

    companion object {
        fun getStartDestination(): String = RegisterScreens.getNestedGraphRootRoute()
    }

    sealed class BottomNavigationScreens(baseRoute: String) : Screen(baseRoute) {
        companion object {
            private const val nestedGraphRootRoute: String = "mainScreens"

            fun getNestedGraphRootRoute(): String = nestedGraphRootRoute

            fun getStartDestination(): String = AllCharactersScreen.route
        }

        object AllCharactersScreen : BottomNavigationScreens(baseRoute = "allCharactersScreen")

        object AllFavoriteCharactersScreen :
            BottomNavigationScreens(baseRoute = "allFavoriteCharactersScreen")

        object ProfileScreen : BottomNavigationScreens(baseRoute = "profileScreen")
    }

    object CharacterScreen : Screen(
        baseRoute = "characterScreen",
        navArgs = listOf(NavItem.Character)
    ) {
        fun createNavRoute(characterId: Int) = "$baseRoute/$characterId"
    }

    object AllCharacterComicsScreen : Screen(
        baseRoute = "allCharacterComicsScreen",
        navArgs = listOf(NavItem.Character, NavItem.ToolbarTitlePrefix)
    ) {
        fun createNavRoute(
            characterId: Int,
            toolbarTitlePrefix: String
        ): String = "$baseRoute/$characterId/$toolbarTitlePrefix"
    }

    object ComicScreen : Screen(
        baseRoute = "comicScreen",
        navArgs = listOf(NavItem.Comic)
    ) {
        fun createNavRoute(comicId: Int): String = "$baseRoute/$comicId"
    }

    sealed class RegisterScreens(baseRoute: String) : Screen(baseRoute) {
        companion object {
            private const val nestedGraphRootRoute: String = "registerScreens"

            fun getNestedGraphRootRoute(): String = nestedGraphRootRoute

            fun getStartDestination(): String = HomeScreen.route
        }

        object HomeScreen : Screen(baseRoute = "homeScreen")


        object LogInScreen : Screen(baseRoute = "logInScreen")


        object SignUpScreen : Screen(baseRoute = "signUpScreen")
    }
}

enum class NavItem(val key: String, val type: NavType<*>) {
    Character(key = "characterId", NavType.IntType),
    Comic(key = "comicId", NavType.IntType),
    ToolbarTitlePrefix(key = "toolbarTitlePrefix", NavType.StringType)
}
