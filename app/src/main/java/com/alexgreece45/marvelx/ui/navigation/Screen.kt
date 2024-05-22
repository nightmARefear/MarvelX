package com.alexgreece45.marvelx.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Represents a unique screen in this application. You can set the screen name through [baseRoute],
 * the arguments through [navArgs], and whether this screen should be included in the auto-logout
 * functionality through [isIncludedInAutoLogout] -- note: [isIncludedInAutoLogout] has a default
 * value of `true`.
 */
sealed class Screen(
    protected val baseRoute: String,
    private val navArgs: List<NavItem> = emptyList(),
    val isIncludedInAutoLogout: Boolean = true
) {
    /**
     * The route of the screen with the navigation arguments included.
     * */
    val route: String
        get() = listOf(baseRoute)
            .plus(navArgs.map { "{${it.key}}" })
            .joinToString("/")

    /**
     * Converts a list of [NavItem]s to a list of [NamedNavArgument]s to be used by
     * NavGraphBuilder#composable.
     */
    val navigationArguments = navArgs.map { navItem ->
        navArgument(name = navItem.key) {
            type = navItem.type
        }
    }

    companion object {
        fun getStartDestination(): String = SplashScreen.route

        /**
         * Returns the correlated [Screen] based on the given [route]. If the [route] doesn't match
         * a specific [Screen], then `null` is returned instead.
         *
         * @param route The route to search the correlated [Screen] of.
         *
         * @return [Screen] if the [route] matches a specific [Screen], otherwise `null`.
         */
        fun fromRoute(route: String): Screen? =
            Screen::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .firstOrNull { screen ->
                    screen.route == route
                }
    }

    // Register screens
    data object SplashScreen :
        Screen(baseRoute = "splashScreen", isIncludedInAutoLogout = false)

    data object IntroLoadingScreen :
        Screen(baseRoute = "introLoadingScreen", isIncludedInAutoLogout = false)

    data object HomeScreen : Screen(baseRoute = "homeScreen", isIncludedInAutoLogout = false)

    data object LoginScreen :
        Screen(baseRoute = "loginScreen", isIncludedInAutoLogout = false)

    data object SignupScreen : Screen(baseRoute = "signupScreen")

    data object ForgotPasswordScreen :
        Screen(baseRoute = "forgotPasswordScreen", isIncludedInAutoLogout = false)

    data object ForgotPasswordValidateEmailScreen :
        Screen(
            baseRoute = "forgotPasswordValidateEmailScreen",
            navArgs = listOf(NavItem.RequestIdEmail)
        ) {
        fun createNavRoute(requestIdEmail: String): String =
            "$baseRoute/$requestIdEmail"
    }

    data object ForgotPasswordEnterPhoneNumberScreen :
        Screen(
            baseRoute = "forgotPasswordEnterPhoneNumberScreen",
            navArgs = listOf(NavItem.RequestIdEmail),
            isIncludedInAutoLogout = false
        ) {
        fun createNavRoute(requestIdEmail: String): String =
            "$baseRoute/$requestIdEmail"
    }

    data object ChangePasswordScreen : Screen(
        baseRoute = "changePasswordScreen",
        navArgs = listOf(NavItem.RequestIdEmail, NavItem.RequestIdPhoneNumber),
        isIncludedInAutoLogout = false
    ) {
        fun createNavRoute(
            requestIdEmail: String,
            requestIdPhoneNumber: String?
        ): String = "$baseRoute/$requestIdEmail/$requestIdPhoneNumber"
    }

    data object OtpScreen : Screen(
        baseRoute = "otpScreen",
        navArgs = listOf(
            NavItem.UserId,
            NavItem.OtpFlowType,
            NavItem.VerificationStep,
            NavItem.ShouldRememberUserSession,
            NavItem.RequestIdEmail,
            NavItem.RequestIdPhoneNumber
        ),
        isIncludedInAutoLogout = false
    ) {
        fun createNavRoute(
            userId: Int? = -1,
            otpFlowType: String,
            verificationStep: String,
            shouldRememberUserSession: Boolean,
            requestIdEmail: String = "-1",
            requestIdPhoneNumber: String = "-1"
        ): String =
            "$baseRoute/$userId/$otpFlowType/$verificationStep/$shouldRememberUserSession/$requestIdEmail/$requestIdPhoneNumber"
    }

    // Main screens --> Bottom Navigation
    data object AllCharactersScreen : Screen(baseRoute = "allCharactersScreen")
    data object AllFavoriteCharactersScreen : Screen(baseRoute = "allFavoriteCharactersScreen")
    data object ProfileScreen : Screen(baseRoute = "profileScreen")

    // Main screens --> Other screens
    data object CharacterScreen : Screen(
        baseRoute = "characterScreen",
        navArgs = listOf(NavItem.CharacterId)
    ) {
        fun createNavRoute(characterId: Int) = "$baseRoute/$characterId"
    }

    data object AllCharacterComicsScreen : Screen(
        baseRoute = "allCharacterComicsScreen",
        navArgs = listOf(NavItem.CharacterId, NavItem.ToolbarTitlePrefix)
    ) {
        fun createNavRoute(
            characterId: Int,
            toolbarTitlePrefix: String
        ): String = "$baseRoute/$characterId/$toolbarTitlePrefix"
    }

    data object ComicScreen : Screen(
        baseRoute = "comicScreen",
        navArgs = listOf(NavItem.ComicId)
    ) {
        fun createNavRoute(comicId: Int): String = "$baseRoute/$comicId"
    }
}

/**
 * A single navigation item that acts as a navigation parameter.
 *
 * @param key           The name of this item.
 * @param type          The type of this item.
 */
enum class NavItem(val key: String, val type: NavType<*>) {
    CharacterId(key = "characterId", type = NavType.IntType),
    ComicId(key = "comicId", type = NavType.IntType),
    ToolbarTitlePrefix(key = "toolbarTitlePrefix", type = NavType.StringType),
    UserId(key = "userId", type = NavType.IntType),
    OtpFlowType(key = "otpFlowType", type = NavType.StringType),
    VerificationStep(key = "verificationStep", type = NavType.StringType),
    ShouldRememberUserSession(key = "shouldRememberUserSession", type = NavType.BoolType),
    RequestIdEmail(key = "requestIdEmail", type = NavType.StringType),
    RequestIdPhoneNumber(key = "requestIdPhoneNumber", type = NavType.StringType),
    IsApplicationInsecure(key = "isApplicationInsecure", type = NavType.BoolType),
}