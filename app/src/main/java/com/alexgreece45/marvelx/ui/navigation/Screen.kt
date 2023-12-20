package com.alexgreece45.marvelx.ui.navigation

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

        data object AllCharactersScreen : BottomNavigationScreens(baseRoute = "allCharactersScreen")

        data object AllFavoriteCharactersScreen :
            BottomNavigationScreens(baseRoute = "allFavoriteCharactersScreen")

        data object ProfileScreen : BottomNavigationScreens(baseRoute = "profileScreen")
    }

    data object CharacterScreen : Screen(
        baseRoute = "characterScreen",
        navArgs = listOf(NavItem.Character)
    ) {
        fun createNavRoute(characterId: Int) = "$baseRoute/$characterId"
    }

    data object AllCharacterComicsScreen : Screen(
        baseRoute = "allCharacterComicsScreen",
        navArgs = listOf(NavItem.Character, NavItem.ToolbarTitlePrefix)
    ) {
        fun createNavRoute(
            characterId: Int,
            toolbarTitlePrefix: String
        ): String = "$baseRoute/$characterId/$toolbarTitlePrefix"
    }

    data object ComicScreen : Screen(
        baseRoute = "comicScreen",
        navArgs = listOf(NavItem.Comic)
    ) {
        fun createNavRoute(comicId: Int): String = "$baseRoute/$comicId"
    }

    sealed class RegisterScreens {
        companion object {
            private const val nestedGraphRootRoute: String = "registerScreens"

            fun getNestedGraphRootRoute(): String = nestedGraphRootRoute

            fun getStartDestination(): String = SplashScreen.route
        }

        data object HomeScreen : Screen(baseRoute = "homeScreen")
        data object LoginScreen : Screen(baseRoute = "loginScreen")
        data object ForgotPasswordScreen : Screen(baseRoute = "forgotPasswordScreen")
        data object SignupScreen : Screen(baseRoute = "signupScreen")
        data object SplashScreen : Screen(baseRoute = "splashScreen")
    }

    data object OtpScreen : Screen(
        baseRoute = "otpScreen",
        navArgs = listOf(NavItem.User, NavItem.VerificationStep, NavItem.ShouldRememberUserSession)
    ) {
        fun createNavRoute(
            userId: Int,
            verificationStep: String,
            shouldRememberUserSession: Boolean
        ): String = "$baseRoute/$userId/$verificationStep/$shouldRememberUserSession"
    }
}

enum class NavItem(val key: String, val type: NavType<*>) {
    Character(key = "characterId", NavType.IntType),
    Comic(key = "comicId", type = NavType.IntType),
    ToolbarTitlePrefix(key = "toolbarTitlePrefix", NavType.StringType),
    User(key = "userId", NavType.IntType),
    VerificationStep(key = "verificationStep", NavType.StringType),
    ShouldRememberUserSession(key = "shouldRememberUserSession", NavType.BoolType)
}