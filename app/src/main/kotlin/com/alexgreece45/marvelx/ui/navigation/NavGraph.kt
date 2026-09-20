package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.data.repository.chat.ChatRepository
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation.conversationScreen
import com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation.navigateToDirectConversationScreen
import com.alexgreece45.marvelx.ui.screens.chat.inbox.navigation.InboxRoute
import com.alexgreece45.marvelx.ui.screens.favoriteListings.navigation.FavoriteListingsRoute
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.CheckoutShippingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.navigation.checkoutSuccessScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.navigation.navigateToCheckoutSuccessScreen
import com.alexgreece45.marvelx.ui.screens.cart.navigation.CartRoute
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordContract
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordViewModel
import com.alexgreece45.marvelx.ui.screens.changePassword.passwordChanged.PasswordChangedScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation.allCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation.navigateToAllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.navigation.characterScreen
import com.alexgreece45.marvelx.ui.screens.comic.navigation.comicScreen
import com.alexgreece45.marvelx.ui.screens.comic.navigation.navigateToComicScreen
import com.alexgreece45.marvelx.ui.screens.dashboard.navigation.dashboardGraph
import com.alexgreece45.marvelx.ui.screens.dashboard.navigation.navigateToDashboardScreen
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.ForgotPasswordScreen
import com.alexgreece45.marvelx.ui.screens.friends.FriendsScreen
import com.alexgreece45.marvelx.ui.screens.friends.FriendsViewModel
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.ui.screens.home.HomeEffect
import com.alexgreece45.marvelx.ui.screens.home.HomeScreen
import com.alexgreece45.marvelx.ui.screens.home.HomeViewModel
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingScreen
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingViewModel
import com.alexgreece45.marvelx.ui.screens.login.LoginEffect
import com.alexgreece45.marvelx.ui.screens.login.LoginScreen
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.createListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.navigateToCreateListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.createListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.navigateToCreateListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.listingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.MarketplaceRoute
import com.alexgreece45.marvelx.ui.screens.myListings.MyListingsScreen
import com.alexgreece45.marvelx.ui.screens.myListings.MyListingsViewModel
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui.screens.orders.OrdersScreen
import com.alexgreece45.marvelx.ui.screens.orders.OrdersViewModel
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersContract
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.navigation.navigateToOrderDetailsScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.navigation.orderDetailsScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.navigateToOrderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.orderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.navigateToOrderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.orderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.requestItemReturnScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.navigation.requestItemReturnSuccessScreen
import com.alexgreece45.marvelx.ui.screens.otp.OtpEffect
import com.alexgreece45.marvelx.ui.screens.otp.OtpScreen
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsContract
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageScreen
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageViewModel
import com.alexgreece45.marvelx.ui.screens.profile.navigation.ProfileRoute
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsContract
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanContract
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanScreen
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanViewModel
import com.alexgreece45.marvelx.ui.screens.settings.SettingsScreen
import com.alexgreece45.marvelx.ui.screens.settings.SettingsViewModel
import com.alexgreece45.marvelx.ui.screens.settings.faq.FaqScreen
import com.alexgreece45.marvelx.ui.screens.settings.faq.FaqViewModel
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsScreen
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsViewModel
import com.alexgreece45.marvelx.ui.screens.signup.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.signup.SignupEffect
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashScreen
import com.alexgreece45.marvelx.ui.transitions.NavigationTransitions
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.extensions.composable
import com.alexgreece45.marvelx.util.extensions.openAppSettings
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Generates the `NavHost` of this application by providing all the necessary routes / screens.
 *
 * @param navController The root [NavHostController] to be used by the `NavHost`.
 */
@Composable
internal fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination(),
        // Applied to every destination that does not declare its own, so the whole graph animates
        // consistently instead of each screen opting in separately.
        enterTransition = NavigationTransitions.enter,
        exitTransition = NavigationTransitions.exit,
        popEnterTransition = NavigationTransitions.popEnter,
        popExitTransition = NavigationTransitions.popExit
    ) {
        // Register screens.
        composable(Screen.SplashScreen) {
            SplashScreen(
                popUpToHomeScreen = {
                    navController.navigate(Screen.HomeScreen.createNavRoute()) {
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
            val viewModel = koinViewModel<IntroLoadingViewModel>()
            IntroLoadingScreen(
                navController = navController,
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect
            )
        }

        composable(Screen.HomeScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                logoutReason = navBackStackEntry.arguments?.getString(NavItem.LogoutReason.key),
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        HomeEffect.Navigation.PopBackStack -> navController.navigateUp()
                        HomeEffect.Navigation.NavigateToLoginScreen -> {
                            navController.navigate(Screen.LoginScreen.route) {
                                launchSingleTop = true
                            }
                        }

                        HomeEffect.Navigation.NavigateToSignupScreen -> {
                            navController.navigate(Screen.SignupScreen.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.LoginScreen) {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        is LoginEffect.Navigation.NavigateToOtpScreen -> {
                            with(navigationEffect) {
                                navController.navigate(
                                    Screen.OtpScreen.createNavRoute(
                                        userId = userId,
                                        otpFlowType = otpFlowType,
                                        verificationStep = verificationStep,
                                        shouldRememberUserSession = shouldRememberUserSession
                                    )
                                )
                            }
                        }

                        LoginEffect.Navigation.NavigateToSignupScreen -> {
                            navController.navigate(Screen.SignupScreen.route) {
                                launchSingleTop = true
                            }
                        }

                        is LoginEffect.Navigation.PopBackStack -> {
                            navController.navigateUp()
                        }

                        LoginEffect.Navigation.NavigateToForgotPasswordScreen -> {
                            navController.navigate(Screen.ForgotPasswordScreen.route)
                        }

                        is LoginEffect.Navigation.NavigateToDashboardScreen -> {
                            navController.navigateToDashboardScreen()
                        }
                    }
                }
            )
        }

        composable(Screen.ForgotPasswordScreen) {
            ForgotPasswordScreen(
                popUpToLoginScreen = navController::navigateUp,
                navController = navController
            )
        }

        composable(Screen.ChangePasswordScreen) { entry ->
            val viewModel = koinViewModel<ChangePasswordViewModel>()
            ChangePasswordScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                snackbarEffects = viewModel.snackbarEffects,
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key),
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        ChangePasswordContract.Effect.Navigation.PopBackStack -> navController.navigateUp()

                        ChangePasswordContract.Effect.Navigation.GoToPasswordChangedScreen -> {
                            navController.navigate(Screen.PasswordChangedScreen.route) {
                                popUpTo(Screen.HomeScreen.route)
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.PasswordChangedScreen) {
            PasswordChangedScreen(
                onGoToLoginClick = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.HomeScreen.route)
                    }
                }
            )
        }

        composable(Screen.SignupScreen) {
            val viewModel = koinViewModel<SignupViewModel>()
            SignUpScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        SignupEffect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        SignupEffect.Navigation.NavigateToDashboardScreen -> {
                            navController.navigateToDashboardScreen()
                        }

                        is SignupEffect.Navigation.NavigateToOtpScreen -> {
                            with(navigationEffect) {
                                navController.navigate(
                                    Screen.OtpScreen.createNavRoute(
                                        userId = userId,
                                        otpFlowType = otpFlowType,
                                        verificationStep = verificationStep,
                                        shouldRememberUserSession = shouldRememberUserSession
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OtpViewModel>()
            OtpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OtpEffect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        OtpEffect.Navigation.NavigateToDashboardScreen -> {
                            navController.navigateToDashboardScreen()
                        }

                        is OtpEffect.Navigation.NavigateToChangePasswordScreen -> {
                            navController.navigate(
                                Screen.ChangePasswordScreen.createNavRoute(
                                    requestIdEmail = navigationEffect.requestIdEmail
                                )
                            )
                        }
                    }
                },
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key),
                otpFlowType = navBackStackEntry.arguments?.getString(NavItem.OtpFlowType.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key),
                shouldRememberUserSession =
                    navBackStackEntry.arguments?.getBoolean(NavItem.ShouldRememberUserSession.key),
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key),
                requestIdPhoneNumber =
                    navBackStackEntry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        // Bottom nav screens.
        dashboardGraph(navController)

        // Main screens.

        // TODO: Resolve later.
//        mainGraph(
//            navController = navController,
//            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
//        )

//        marketplaceScreen(
//            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
//            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
//        )

        listingDetailsScreen(
            goBack = navController::navigateUp,
            navigateToProfileDetailsScreen = { userId ->
                navController.navigate(
                    Screen.ProfileDetailsScreen.createNavRoute(
                        userId = userId
                    )
                )
            },
            navigateToEditListingScreen = navController::navigateToCreateListingWizardScreen,
            navigateToOrderItemManagementScreen = navController::navigateToOrderItemManagementScreen,
            navigateToOrderItemTrackingHistoryScreen = navController::navigateToOrderItemTrackingHistoryScreen
        )

        createListingWizardScreen(
            goBack = navController::navigateUp,
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            navigateToCreateListingSuccessScreen = navController::navigateToCreateListingSuccessScreen
        )

        createListingSuccessScreen(navController = navController)

        characterScreen(
            goBack = navController::navigateUp,
            navigateToComicScreen = navController::navigateToComicScreen,
            navigateToAllCharacterComicsScreen = navController::navigateToAllCharacterComicsScreen,
        )

        composable(
            screen = Screen.MyListingsScreen,
        ) {
            val viewModel = koinViewModel<MyListingsViewModel>()
            MyListingsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        MyListingsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        MyListingsContract.Effect.Navigation.GoToCreateListingScreen -> {
                            navController.navigateToCreateListingWizardScreen(null)
                        }

                        is MyListingsContract.Effect.Navigation.GoToEditListingScreen -> {
                            navController.navigateToCreateListingWizardScreen(navigationEffect.listingId)
                        }

                        is MyListingsContract.Effect.Navigation.GoToListingDetailsScreen -> {
                            navController.navigateToListingDetailsScreen(navigationEffect.listingId)
                        }
                    }
                },
                snackbarEffects = viewModel.snackbarEffects
            )
        }

//        cartScreen(
//            navController = navController,
//            rootNavController = navController,
//            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
//        )

        composable(screen = Screen.CheckoutWizardScreen) {
            val viewModel = koinViewModel<CheckoutWizardViewModel>()
            CheckoutWizardScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        CheckoutWizardContract.Effect.Navigation.ExitWizard -> navController.navigateUp()
                        CheckoutWizardContract.Effect.Navigation.NavigateToMarketplaceScreen -> {
                            navController.navigateToDashboardScreen()
                        }

                        is CheckoutWizardContract.Effect.Navigation.NavigateToOrderDetailsScreen -> {
                            navController.navigateToOrderDetailsScreen(
                                orderId = navigationEffect.orderId,
                                navOptionsBuilder = {
                                    popUpTo(Screen.CheckoutWizardScreen) {
                                        inclusive = true
                                    }
                                }
                            )
                        }

                        is CheckoutWizardContract.Effect.Navigation.NavigateToCheckoutSuccessScreen -> {
                            navController.navigateToCheckoutSuccessScreen(
                                orderId = navigationEffect.orderId,
                                navOptionsBuilder = {
                                    popUpTo(Screen.CheckoutWizardScreen.route) { inclusive = true }
                                }
                            )
                        }
                    }
                },
                checkoutShippingDetailsViewModel = koinViewModel<CheckoutShippingDetailsViewModel>()
            )
        }

        checkoutSuccessScreen(
            goBack = navController::navigateUp,
            navigateToOrderDetailsScreen = navController::navigateToOrderDetailsScreen
        )

        composable(
            screen = Screen.OrdersScreen,
        ) {
            val viewModel = koinViewModel<OrdersViewModel>()
            OrdersScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OrdersContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        is OrdersContract.Effect.Navigation.GoToOrderDetailsScreen -> {
                            navController.navigateToOrderDetailsScreen(navigationEffect.orderId)
                        }

                        OrdersContract.Effect.Navigation.GoToMarketplaceScreen -> navController.navigateToDashboardScreen()
                    }
                },
            )
        }

        orderDetailsScreen(
            goBack = navController::navigateUp,
            navigateToOrderItemTrackingHistoryScreen = navController::navigateToOrderItemTrackingHistoryScreen
        )

        orderItemTrackingHistoryScreen(navController = navController)

        orderItemManagementScreen(navController = navController)

        conversationScreen(navController = navController)

        requestItemReturnScreen(navController = navController)

        requestItemReturnSuccessScreen(navController = navController)

//        allCharactersScreen(
//            navController = navController,
//            navigateToCharacterScreen = { characterId ->
//                navController.navigateToCharacterScreen(
//                    characterId = characterId,
//                    navOptionsBuilder = {
//                        navOptions {
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            },
//            openDeepLinkAction = navController::openDeepLinkAction
//        )

        comicScreen(goBack = navController::navigateUp)

        allCharacterComicsScreen(
            goBack = navController::navigateUp,
            navigateToComicScreen = { comicId ->
                navController.navigateToComicScreen(comicId)
            }
        )


        composable(
            screen = Screen.FriendsScreen,
        ) {
            val viewModel = koinViewModel<FriendsViewModel>()
            FriendsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                snackbarEffects = viewModel.snackbarEffects,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        FriendsContract.Effect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        is FriendsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                            navController.navigate(
                                Screen.ProfileDetailsScreen.createNavRoute(userId = navigationEffect.userId)
                            )
                        }
                    }
                }
            )
        }

//        profileScreen(
//            navController = navController,
//            rootNavController = navController
//        )

        composable(
            screen = Screen.ProfileImageScreen,
            enterTransition = NavigationTransitions.modalEnter,
            exitTransition = NavigationTransitions.modalExit,
            popEnterTransition = NavigationTransitions.modalPopEnter,
            popExitTransition = NavigationTransitions.modalPopExit
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileImageViewModel>()
            ProfileImageScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateUp = navController::navigateUp,
                profileImageUrl = navBackStackEntry.arguments?.getString(NavItem.ProfileImageUrl.key)
                    .orEmpty()
            )
        }

        composable(
            screen = Screen.ProfileDetailsScreen,
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsViewModel>()
            ProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                snackbarEffects = viewModel.snackbarEffects,
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key).orEmpty(),
                shouldShowAcceptFriendRequestDialog = navBackStackEntry.arguments?.getBoolean(
                    NavItem.ShouldShowAcceptFriendRequestDialog.key
                ) ?: false,
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        ProfileDetailsContract.Effect.Navigation.PopBackStack -> navController.navigateUp()

                        is ProfileDetailsContract.Effect.Navigation.NavigateToProfileImageScreen -> {
                            navController.navigate(
                                Screen.ProfileImageScreen.createNavRoute(
                                    navigationEffect.profileImageUrl
                                )
                            )
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

                        is ProfileDetailsContract.Effect.Navigation.NavigateToConversation -> {
                            navController.navigateToDirectConversationScreen(navigationEffect.userId)
                        }
                    }
                },
                snackbarNavigationPayloadFlow = navBackStackEntry.savedStateHandle
                    .getStateFlow<SnackbarNavigationPayload?>(
                        key = SavedStateHandleKeys.snackbarNavigationPayload,
                        initialValue = null
                    ),
                onSnackbarNavigationPayloadConsumed = {
                    navBackStackEntry.savedStateHandle.remove<SnackbarNavigationPayload>(
                        SavedStateHandleKeys.snackbarNavigationPayload
                    )
                }
            )
        }

        composable(Screen.ProfileDetailsStatsScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsStatsViewModel>()
            ProfileDetailsStatsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                fullName = navBackStackEntry.arguments?.getString(NavItem.FullName.key).orEmpty(),
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key).orEmpty(),
                tabIndex = navBackStackEntry.arguments?.getInt(NavItem.TabIndex.key) ?: 0,
                onNavigationEffect = { effect ->
                    when (effect) {
                        ProfileDetailsStatsContract.Effect.Navigation.NavigateUp -> navController.navigateUp()
                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> navController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(userId = effect.userId)
                        )

                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToListingScreen -> TODO(
                            "Not implemented yet."
                        )

                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToCharacterScreen -> navController.navigate(
                            Screen.CharacterScreen.createNavRoute(characterId = effect.characterId)
                        )
                    }
                }
            )
        }

        composable(
            screen = Screen.EditProfileDetailsScreen,
        ) {
            val viewModel = koinViewModel<EditProfileDetailsViewModel>()
            EditProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        EditProfileDetailsContract.Effect.Navigation.NavigateUp -> {
                            navController.navigateUp()
                        }

                        is EditProfileDetailsContract.Effect.Navigation.NavigateUpWithSnackbarPayload -> {
                            navController.previousBackStackEntry?.savedStateHandle[SavedStateHandleKeys.snackbarNavigationPayload] =
                                navigationEffect.snackbarNavigationPayload
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable(
            screen = Screen.SettingsScreen,
        ) {
            val viewModel = koinViewModel<SettingsViewModel>()

            SettingsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = {
                    when (it) {
                        SettingsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        SettingsContract.Effect.Navigation.NavigateToFaqScreen -> navController.navigate(
                            Screen.FaqScreen.route
                        )

                        SettingsContract.Effect.Navigation.NavigateToHomeScreen -> {
                            navController.navigate(
                                Screen.HomeScreen.createNavRoute(LogoutReason.USER_TRIGGERED.name)
                            ) {
                                popUpToTop(navController)
                            }
                        }

                        SettingsContract.Effect.Navigation.NavigateToPrivacySettingsScreen -> navController.navigate(
                            Screen.PrivacySettingsScreen.route
                        )
                    }
                }
            )
        }

        composable(
            screen = Screen.PrivacySettingsScreen,
        ) {
            val viewModel = koinViewModel<PrivacySettingsViewModel>()
            PrivacySettingsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::navigateUp,
            )
        }

        composable(
            screen = Screen.FaqScreen,
        ) {
            val viewModel = koinViewModel<FaqViewModel>()
            FaqScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateUp = navController::navigateUp,
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
                    navController.openDeepLinkAction(deepLinkAction)
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
fun getBottomNavigationBarData(navController: NavController): BottomNavigationBarData {
    val unreadMessages = koinInject<ChatRepository>().unreadCount.collectAsStateWithLifecycle().value

    return BottomNavigationBarData(
        navController = navController,
        rootItem = rootBottomBarItem,
        items = getBottomNavigationItems(unreadMessages = unreadMessages),
        backgroundColor = MaterialTheme.colors.background,
        topHorizontalColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
        selectedItemTopHorizontalColor = MaterialTheme.customColors.genericRed.copy(alpha = 0.8f),
        elevation = SPACING_MEDIUM.dp
    )
}

private val rootBottomBarItem = BottomBarItem(
    route = MarketplaceRoute,
    label = R.string.bottom_navigation_label_marketplace,
    iconData = IconData(
        iconResource = R.drawable.ic_marketplace,
        contentDescription = R.string.bottom_navigation_label_marketplace
    )
)

/**
 * Returns the bottom navigation items.
 */
private fun getBottomNavigationItems(unreadMessages: Int): List<BottomBarItem> = listOf(
    rootBottomBarItem,
    BottomBarItem(
        route = InboxRoute,
        label = R.string.bottom_navigation_label_inbox,
        iconData = IconData(
            iconResource = R.drawable.ic_chat,
            contentDescription = R.string.bottom_navigation_label_inbox
        ),
        badgeCount = unreadMessages
    ),
    BottomBarItem(
        route = FavoriteListingsRoute,
        label = R.string.bottom_navigation_label_favorites,
        iconData = IconData(
            iconResource = R.drawable.ic_favorite_filled,
            contentDescription = R.string.bottom_navigation_label_favorites
        )
    ),
    BottomBarItem(
        route = CartRoute,
        label = R.string.bottom_navigation_label_cart,
        iconData = IconData(
            iconResource = R.drawable.ic_cart,
            contentDescription = R.string.bottom_navigation_label_cart
        )
    ),
    BottomBarItem(
        route = ProfileRoute(),
        label = R.string.bottom_navigation_label_profile,
        iconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.bottom_navigation_label_profile
        )
    )
)