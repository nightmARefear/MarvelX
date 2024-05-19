package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharactersScreen.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.comicScreen.ComicScreen
import com.alexgreece45.marvelx.ui.screens.introLoadingScreen.IntroLoadingScreen
import com.alexgreece45.marvelx.ui.screens.otpScreen.OtpScreen
import com.alexgreece45.marvelx.ui.screens.profileScreen.ProfileScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen.HomeScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.LoginScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen.ForgotPasswordScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen.forgotPasswordValidateEmailScreen.ForgotPasswordValidateEmailScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.signupScreen.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.splashScreen.SplashScreen
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.extensions.composable
import com.alexgreece45.marvelx.util.extensions.popUpToTop

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        composable(Screen.SplashScreen) {
            SplashScreen(navController = navController)
        }

        composable(Screen.IntroLoadingScreen) {
            IntroLoadingScreen(navController = navController)
        }

        composable(Screen.HomeScreen) {
            HomeScreen(navController = navController)
        }

        composable(Screen.LoginScreen) {
            LoginScreen(
                navController = navController
            )
        }

        composable(Screen.ForgotPasswordScreen) {
            ForgotPasswordScreen(
                popUpToLoginScreen = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Screen.ForgotPasswordValidateEmailScreen) { navBackStackEntry ->
            ForgotPasswordValidateEmailScreen(
                popBackStack = { navController.popBackStack() },
                navigateToForgotPasswordEnterPhoneNumberScreen = { requestIdEmail ->
                    navController.navigate(
                        Screen.ForgotPasswordEnterPhoneNumberScreen.createNavRoute(
                            requestIdEmail = requestIdEmail
                        )
                    )
                },
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key)
            )
        }

        composable(Screen.ChangePasswordScreen) { entry ->
            ChangePasswordScreen(
                popBackStack = { navController.popBackStack() },
                otpFlowType = entry.arguments?.getString(NavItem.OtpFlowType.key),
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key).orEmpty(),
                requestIdPhoneNumber = entry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        composable(Screen.SignupScreen) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            OtpScreen(
                navController = navController,
                userId = navBackStackEntry.arguments?.getInt(NavItem.UserId.key),
                otpFlowType = navBackStackEntry.arguments?.getString(NavItem.OtpFlowType.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key),
                shouldRememberUserSession = navBackStackEntry.arguments?.getBoolean(NavItem.ShouldRememberUserSession.key),
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key),
                requestIdPhoneNumber = navBackStackEntry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        // Main screens.
        composable(screen = Screen.AllCharactersScreen) {
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

        composable(Screen.AllFavoriteCharactersScreen) {
            AllFavoriteCharactersScreen(
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }

        composable(Screen.ProfileScreen) {
            ProfileScreen(
                navigateToHomeScreen = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpToTop(navController)
                    }
                },
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }
    }
}

/**
 * Returns the bottom navigation items.
 */
private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
    BottomBarItem(
        route = Screen.AllCharactersScreen.route,
        label = R.string.bottom_navigation_label_characters,
        iconData = IconData(
            iconResource = R.drawable.ic_superhero,
            contentDescription = R.string.bottom_navigation_label_characters
        )
    ),
    BottomBarItem(
        route = Screen.AllFavoriteCharactersScreen.route,
        label = R.string.bottom_navigation_label_favorites,
        iconData = IconData(
            iconResource = R.drawable.ic_favorite_filled,
            contentDescription = R.string.bottom_navigation_label_favorites
        )
    ),
    BottomBarItem(
        route = Screen.ProfileScreen.route,
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
        elevation = SPACING_MEDIUM.dp
    )