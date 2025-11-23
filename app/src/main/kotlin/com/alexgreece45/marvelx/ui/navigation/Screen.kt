package com.alexgreece45.marvelx.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.alexgreece45.marvelx.ui.helper.createUriPatternForDeepLink

/**
 * Represents a unique screen in this application. You can set the screen name through [baseRoute],
 * the arguments through [navArgs], and whether this screen should be included in the auto-logout
 * functionality through [isIncludedInAutoLogout] -- note: [isIncludedInAutoLogout] has a default
 * value of `true`.
 *
 * @param baseRoute                 The base route of this route.
 * @param navArgs                   The navigation arguments of this route.
 * @param deepLinkArgs              The deep link arguments of this route.
 * @param isIncludedInAutoLogout    Whether this route is included in the auto-logout functionality.
 * It has the default value of `true`.
 */
sealed class Screen(
    val baseRoute: String,
    private val navArgs: List<NavItem> = emptyList(),
    val deepLinkArgs: List<NavDeepLinkItem> = emptyList(),
    val isIncludedInAutoLogout: Boolean = true
) {
    /**
     * The route of the screen with the navigation arguments included.
     */
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

    /**
     * The `deepLinks` object for the graph builder.
     */
    val deepLinks = if (deepLinkArgs.isNotEmpty()) {
        listOf(
            navDeepLink {
                uriPattern = createUriPatternForDeepLink(baseRoute, navArgs)
            }
        )
    } else emptyList()

    companion object {
        /**
         * Returns the start destination of the root nav graph.
         */
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

        /**
         * Attempts to match the given [screenRoute] against a predefined selection of
         * deeplink-supported screens and returns the requested [Screen] if it was found, otherwise
         * `null`.
         */
        fun matchDeepLinkScreenRoute(screenRoute: String): Screen? =
            when (screenRoute) {
                ProfileScreen.baseRoute -> ProfileScreen
                ProfileDetailsScreen.baseRoute -> ProfileDetailsScreen
                else -> null
            }

        /**
         * Checks whether the given [deepLinkArgs] matches the ones of the given [Screen].
         */
        fun doDeepLinkArgsMatch(screen: Screen, deepLinkArgs: List<NavDeepLinkItem>): Boolean =
            deepLinkArgs == screen.deepLinkArgs
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
            userId: String? = "",
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
    data object ProfileScreen : Screen(
        baseRoute = "profileScreen",
        navArgs = listOf(NavItem.QrCodeScanErrorMessage),
        deepLinkArgs = listOf(NavDeepLinkItem.QrCodeScanErrorMessage)
    ) {
        fun createNavRoute(qrCodeScanErrorMessage: String): String =
            "$baseRoute/$qrCodeScanErrorMessage"
    }

    data object ProfileImageScreen : Screen(
        baseRoute = "profileImageScreen",
        navArgs = listOf(NavItem.ProfileImageUrl)
    ) {
        fun createNavRoute(userId: String): String =
            "$baseRoute/$userId"
    }

    /**
     * Profile Screen --> Profile Details
     */
    data object ProfileDetailsScreen :
        Screen(
            baseRoute = "profileDetailsScreen",
            navArgs = listOf(NavItem.UserId, NavItem.ShouldShowAcceptFriendRequestDialog),
            deepLinkArgs = listOf(
                NavDeepLinkItem.UserId,
                NavDeepLinkItem.ShouldShowAcceptFriendRequestDialog
            )
        ) {
        fun createNavRoute(
            userId: String,
        ): String = "$baseRoute/$userId/false"
    }

    /**
     * Friends Screen
     */
    data object FriendsScreen : Screen(baseRoute = "friendsScreen")

    /**
     * Profile Screen --> Profile Details --> Edit Profile Details
     */
    data object EditProfileDetailsScreen : Screen(baseRoute = "editProfileDetailsScreen")

    /**
     * Profile Screen --> Profile Details --> Profile Details Stats
     */
    data object ProfileDetailsStatsScreen : Screen(
        baseRoute = "profileDetailsStatsScreen",
        navArgs = listOf(NavItem.FullName, NavItem.UserId, NavItem.TabIndex)
    ) {
        fun createNavRoute(fullName: String, userId: String, tabIndex: Int): String =
            "$baseRoute/$fullName/$userId/$tabIndex"
    }

    data object SettingsScreen : Screen(
        baseRoute = "settingsScreen"
    )

    /**
     * Settings Screen --> FAQ
     */
    data object FaqScreen : Screen(baseRoute = "faqScreen")

    /**
     * Settings Screen --> Privacy Settings
     */
    data object PrivacySettingsScreen : Screen(baseRoute = "privacySettingsScreen")

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

    data object QrCodeScreen : Screen(
        baseRoute = "qrCodeScreen",
        navArgs = listOf(NavItem.PreviousScreenRoute)
    ) {
        fun createNavRoute(previousScreenRoute: String): String = "$baseRoute/$previousScreenRoute"
    }
}

/**
 * A single navigation item that acts as a navigation parameter.
 *
 * @param key   The name of this item.
 * @param type  The type of this item.
 */
enum class NavItem(val key: String, val type: NavType<*>) {
    CharacterId(key = NavItemKey.characterId, type = NavType.IntType),
    ComicId(key = NavItemKey.comicId, type = NavType.IntType),
    ToolbarTitlePrefix(key = NavItemKey.toolbarTitlePrefix, type = NavType.StringType),
    UserId(key = NavItemKey.userId, type = NavType.StringType),
    OtpFlowType(key = NavItemKey.otpFlowType, type = NavType.StringType),
    VerificationStep(key = NavItemKey.verificationStep, type = NavType.StringType),
    ShouldRememberUserSession(key = NavItemKey.shouldRememberUserSession, type = NavType.BoolType),
    RequestIdEmail(key = NavItemKey.requestIdEmail, type = NavType.StringType),
    RequestIdPhoneNumber(key = NavItemKey.requestIdPhoneNumber, type = NavType.StringType),
    FullName(key = NavItemKey.fullName, type = NavType.StringType),
    TabIndex(key = NavItemKey.tabIndex, type = NavType.IntType),
    PreviousScreenRoute(key = NavItemKey.previousScreenRoute, type = NavType.StringType),
    QrCodeScanErrorMessage(key = NavItemKey.qrCodeScanErrorMessage, type = NavType.StringType),
    ProfileImageUrl(key = NavItemKey.profileImageUrl, type = NavType.StringType),
    ShouldShowAcceptFriendRequestDialog(
        key = NavItemKey.shouldShowAcceptFriendRequestDialog,
        type = NavType.BoolType
    ),
}

/**
 * Represents the different types of deep link items used for navigation.
 */
enum class NavDeepLinkItem(val key: String) {
    UserId(key = NavItemKey.userId),
    ListingId(key = NavItemKey.listingId),
    QrCodeScanErrorMessage(key = NavItemKey.qrCodeScanErrorMessage),
    ShouldShowAcceptFriendRequestDialog(key = NavItemKey.shouldShowAcceptFriendRequestDialog),
}

/**
 * Contains the keys used for normal and deep link navigation arguments. This object provides
 * consistent naming, and compile-time hardcoded values.
 */
private object NavItemKey {
    const val characterId = "characterId"
    const val comicId = "comicId"
    const val toolbarTitlePrefix = "toolbarTitlePrefix"
    const val userId = "userId"
    const val otpFlowType = "otpFlowType"
    const val verificationStep = "verificationStep"
    const val shouldRememberUserSession = "shouldRememberUserSession"
    const val requestIdEmail = "requestIdEmail"
    const val requestIdPhoneNumber = "requestIdPhoneNumber"
    const val fullName = "fullName"
    const val tabIndex = "tabIndex"
    const val listingId = "listingId"
    const val qrCodeScanErrorMessage = "qrCodeScanErrorMessage"
    const val previousScreenRoute = "previousScreenRoute"
    const val profileImageUrl = "profileImageUrl"
    const val shouldShowAcceptFriendRequestDialog = "shouldShowAcceptFriendRequestDialog"
}
