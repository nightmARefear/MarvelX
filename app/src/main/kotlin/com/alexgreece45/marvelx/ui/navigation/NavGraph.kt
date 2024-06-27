package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.character.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.character.CharacterViewModel
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsViewModel
import com.alexgreece45.marvelx.ui.screens.comic.ComicScreen
import com.alexgreece45.marvelx.ui.screens.comic.ComicViewModel
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.ForgotPasswordScreen
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.forgotPasswordValidateEmailScreen.ForgotPasswordValidateEmailScreen
import com.alexgreece45.marvelx.ui.screens.home.HomeScreen
import com.alexgreece45.marvelx.ui.screens.home.HomeViewModel
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingScreen
import com.alexgreece45.marvelx.ui.screens.login.LoginScreen
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.otp.OtpScreen
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.ProfileContract
import com.alexgreece45.marvelx.ui.screens.profile.ProfileScreen
import com.alexgreece45.marvelx.ui.screens.profile.ProfileViewModel
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.faq.FaqScreen
import com.alexgreece45.marvelx.ui.screens.profile.faq.FaqViewModel
import com.alexgreece45.marvelx.ui.screens.profile.privacySettings.PrivacySettingsScreen
import com.alexgreece45.marvelx.ui.screens.profile.privacySettings.PrivacySettingsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsViewModel
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanContract
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanScreen
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanViewModel
import com.alexgreece45.marvelx.ui.screens.signup.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashScreen
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.extensions.composable
import com.alexgreece45.marvelx.util.extensions.openAppSettings
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import org.koin.androidx.compose.koinViewModel

/**
 * Generates the `NavHost` of this application by providing all the necessary routes / screens.
 *
 * @param navController The root [NavHostController] to be used by the `NavHost`.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        composable(Screen.SplashScreen) {
            SplashScreen(
                popUpToHomeScreen = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpToTop(navController)
                    }
                },
                popUpToIntroLoadingScreen = {
                    navController.navigate(Screen.IntroLoadingScreen.route) {
                        popUpToTop(navController)
                    }
                }
            )
        }

        composable(Screen.IntroLoadingScreen) {
            IntroLoadingScreen(navController = navController)
        }

        composable(Screen.HomeScreen) {
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToLoginScreen = {
                    navController.navigate(Screen.LoginScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToSignupScreen = {
                    navController.navigate(Screen.SignupScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToOtpScreen = { userId, otpFlowType, verificationStep, shouldRememberUserSession ->
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = userId,
                            otpFlowType = otpFlowType,
                            verificationStep = verificationStep,
                            shouldRememberUserSession = shouldRememberUserSession
                        )
                    )
                },
                popBackStack = navController::popBackStack
            )
        }

        composable(Screen.LoginScreen) {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToSignupScreen = {
                    navController.navigate(Screen.SignupScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToOtpScreen = { userId, otpFlowType, verificationStep, shouldRememberUserSession ->
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = userId,
                            otpFlowType = otpFlowType,
                            verificationStep = verificationStep,
                            shouldRememberUserSession = shouldRememberUserSession
                        )
                    )
                },
                navigateToForgotPasswordScreen = {
                    navController.navigate(Screen.ForgotPasswordScreen.route)
                },
                popBackStack = navController::popBackStack
            )
        }

        composable(Screen.ForgotPasswordScreen) {
            ForgotPasswordScreen(
                popUpToLoginScreen = navController::popBackStack,
                navController = navController
            )
        }

        composable(Screen.ForgotPasswordValidateEmailScreen) { navBackStackEntry ->
            ForgotPasswordValidateEmailScreen(
                popBackStack = navController::popBackStack,
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
                popBackStack = navController::popBackStack,
                otpFlowType = entry.arguments?.getString(NavItem.OtpFlowType.key),
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key).orEmpty(),
                requestIdPhoneNumber = entry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        composable(Screen.SignupScreen) {
            val viewModel = koinViewModel<SignupViewModel>()
            SignUpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToOtpScreen = { userId, otpFlowType, verificationStep, shouldRememberUserSession ->
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = userId,
                            otpFlowType = otpFlowType,
                            verificationStep = verificationStep,
                            shouldRememberUserSession = shouldRememberUserSession
                        )
                    )
                },
                popBackStack = navController::popBackStack
            )
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OtpViewModel>()
            OtpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToChangePasswordScreen = { requestIdEmail, requestIdPhoneNumber ->
                    navController.navigate(
                        Screen.ChangePasswordScreen.createNavRoute(
                            requestIdEmail = requestIdEmail,
                            requestIdPhoneNumber = requestIdPhoneNumber
                        )
                    )
                },
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
            val viewModel = koinViewModel<AllCharactersViewModel>()
            AllCharactersScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
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
            val viewModel = koinViewModel<CharacterViewModel>()
            CharacterScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key)
                    ?: -1,
                popBackStack = navController::popBackStack,
                navigateToComicsScreen = { comicId ->
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = comicId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                navigateToAllComicsScreen = { characterId, toolbarTitlePrefix ->
                    navController.navigate(
                        Screen.AllCharacterComicsScreen.createNavRoute(
                            characterId = characterId,
                            toolbarTitlePrefix = toolbarTitlePrefix
                        )
                    )
                }
            )
        }

        composable(
            screen = Screen.ComicScreen
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ComicViewModel>()
            ComicScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
                comicId = navBackStackEntry.arguments?.getInt(NavItem.ComicId.key) ?: -1
            )
        }

        composable(
            screen = Screen.AllCharacterComicsScreen
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<AllCharacterComicsViewModel>()
            AllCharacterComicsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
                navigateToComicScreen = { comicId ->
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = comicId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key) ?: -1,
                toolbarTitlePrefix = navBackStackEntry.arguments?.getString(NavItem.ToolbarTitlePrefix.key)
                    .orEmpty()
            )
        }

        composable(Screen.AllFavoriteCharactersScreen) {
            val viewModel = koinViewModel<AllFavoriteCharactersViewModel>()
            AllFavoriteCharactersScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }

        composable(Screen.ProfileScreen) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) },
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        ProfileContract.Effect.Navigation.NavigateToHomeScreen -> {
                            navController.navigate(Screen.HomeScreen.route) {
                                popUpToTop(navController)
                            }
                        }

                        is ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                            navController.navigate(
                                Screen.ProfileDetailsScreen.createNavRoute(
                                    userId = navigationEffect.userId
                                )
                            )
                        }

                        ProfileContract.Effect.Navigation.NavigateToFaqScreen -> {
                            navController.navigate(Screen.FaqScreen.route)
                        }

                        ProfileContract.Effect.Navigation.NavigateToPrivacySettingsScreen -> {
                            navController.navigate(Screen.PrivacySettingsScreen.route)
                        }

                        is ProfileContract.Effect.Navigation.NavigateToQrCodeScreen -> {
                            navController.navigate(
                                Screen.QrCodeScreen.createNavRoute(
                                    navigationEffect.previousScreenRoute
                                )
                            )
                        }
                    }
                },
                openDeepLinkAction = { deepLinkAction ->
                    openDeepLinkAction(
                        navController = navController,
                        deepLinkAction = deepLinkAction
                    )
                }
            )
        }

        composable(Screen.ProfileDetailsScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsViewModel>()
            ProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                userId = navBackStackEntry.arguments?.getInt(NavItem.UserId.key) ?: -1,
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        ProfileDetailsContract.Effect.Navigation.PopBackStack -> {
                            navController.popBackStack()
                        }

                        ProfileDetailsContract.Effect.Navigation.NavigateToEditProfileDetailsScreen -> {
                            navController.navigate(Screen.EditProfileDetailsScreen.route)
                        }

                        is ProfileDetailsContract.Effect.Navigation.NavigateToProfileDetailsStatsScreen -> {
                            navController.navigate(
                                Screen.ProfileDetailsStatsScreen.createNavRoute(
                                    fullName = navigationEffect.fullName,
                                    userId = navigationEffect.userId,
                                    tabIndex = navigationEffect.tab.tabIndex
                                )
                            )
                        }
                    }
                },
            )
        }

        composable(Screen.ProfileDetailsStatsScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsStatsViewModel>()
            ProfileDetailsStatsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                fullName = navBackStackEntry.arguments?.getString(NavItem.FullName.key).orEmpty(),
                userId = navBackStackEntry.arguments?.getInt(NavItem.UserId.key) ?: -1,
                tabIndex = navBackStackEntry.arguments?.getInt(NavItem.TabIndex.key) ?: 0,
                navigateUp = navController::navigateUp,
            )
        }

        composable(Screen.EditProfileDetailsScreen) {
            val viewModel = koinViewModel<EditProfileDetailsViewModel>()
            EditProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
            )
        }

        composable(Screen.PrivacySettingsScreen) {
            val viewModel = koinViewModel<PrivacySettingsViewModel>()
            PrivacySettingsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
            )
        }

        composable(Screen.FaqScreen) {
            val viewModel = koinViewModel<FaqViewModel>()
            FaqScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::popBackStack,
            )
        }

        composable(Screen.QrCodeScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<QrCodeScanViewModel>()
            QrCodeScanScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                previousScreenRoute = navBackStackEntry.arguments?.getString(NavItem.PreviousScreenRoute.key)
                    .orEmpty(),
                onNavigation = {
                    when (it) {
                        QrCodeScanContract.Effect.Navigation.NavigateUp -> navController.navigateUp()

                        QrCodeScanContract.Effect.Navigation.OpenAppSettings -> {
                            navController.context.openAppSettings()
                        }
                    }
                },
                navigateUpAndOpenDeepLinkAction = { deepLinkAction ->
                    navController.navigateUp()
                    openDeepLinkAction(
                        navController = navController,
                        deepLinkAction = deepLinkAction
                    )
                },
                navigateUp = navController::navigateUp
            )
        }
    }
}

/**
 * Returns the [BottomNavigationBarData] required for the bottom navigation bar in the main screens.
 *
 * @param navController The associated [NavController] of the graph.
 */
@Composable
fun getBottomNavigationBarData(navController: NavController): BottomNavigationBarData =
    BottomNavigationBarData(
        navController = navController,
        items = getBottomNavigationItems(),
        backgroundColor = MaterialTheme.colors.background,
        topHorizontalColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
        selectedItemTopHorizontalColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.8f),
        elevation = SPACING_MEDIUM.dp
    )

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
